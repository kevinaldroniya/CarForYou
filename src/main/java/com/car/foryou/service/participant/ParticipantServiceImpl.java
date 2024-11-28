package com.car.foryou.service.participant;

import com.car.foryou.dto.auction.AuctionProcessStatus;
import com.car.foryou.dto.notification.MessageTemplate;
import com.car.foryou.dto.notification.NotificationChannel;
import com.car.foryou.dto.participant.ParticipantResponse;
import com.car.foryou.exception.InvalidRequestException;
import com.car.foryou.exception.ResourceAlreadyExistsException;
import com.car.foryou.exception.ResourceExpiredException;
import com.car.foryou.exception.ResourceNotFoundException;
import com.car.foryou.mapper.ParticipantMapper;
import com.car.foryou.model.Auction;
import com.car.foryou.model.Item;
import com.car.foryou.model.Participant;
import com.car.foryou.model.User;
import com.car.foryou.repository.participant.ParticipantRepository;
import com.car.foryou.service.auction.AuctionService;
import com.car.foryou.service.notification.NotificationService;
import com.car.foryou.service.item.ItemService;
import com.car.foryou.service.user.CustomUserDetailService;
import com.car.foryou.service.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class ParticipantServiceImpl implements ParticipantService {
    private final ParticipantRepository participantRepository;
    private final AuctionService auctionService;
    private final ItemService itemService;
    private final NotificationService notificationService;
    private final UserService userService;

    private static final String PARTICIPANT = "PARTICIPANT";

    public ParticipantServiceImpl(ParticipantRepository participantRepository, AuctionService auctionService, ItemService itemService, NotificationService notificationService, UserService userService) {
        this.participantRepository = participantRepository;
        this.auctionService = auctionService;
        this.itemService = itemService;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @Override
    public List<Participant> getAllParticipantsV2() {
        return participantRepository.findAll();
    }

    @Override
    public ParticipantResponse getParticipantResponseV2(Integer participantId) {
        return ParticipantMapper.mapToAuctionParticipantResponse(getParticipantByIdV2(participantId));
    }

    @Override
    public Participant getParticipantByIdV2(Integer participantId) {
        return participantRepository.findById(participantId).orElseThrow(
                () -> new ResourceNotFoundException(PARTICIPANT, "ID", participantId)
        );
    }

    @Override
    public ParticipantResponse createParticipant(Integer auctionId) {
        Integer userId = CustomUserDetailService.getLoggedInUserDetails().getId();
        participantRepository.findByAuctionIdAndUserId(auctionId, userId).ifPresent(
                participant -> {
                    throw new ResourceAlreadyExistsException(PARTICIPANT, HttpStatus.CONFLICT);
                }
        );
        Auction auctionById = auctionService.getAuctionById(auctionId);
        Participant participant = Participant.builder()
                .auction(auctionById)
                .user(User.builder().id(userId).build())
                .depositStatus(Participant.DepositStatus.PAID)
                .build();
        return ParticipantMapper.mapToAuctionParticipantResponse(participantRepository.save(participant));
    }

    @Override
    public Participant updateParticipantV2(Integer participantId) {
        return null;
    }

    @Override
    public Participant deleteParticipant(Integer participantId) {
        return null;
    }

    @Override
    public List<Participant> getParticipantByAuctionId(Integer auctionId) {
        return List.of();
    }

    @Override
    public Participant updateDepositStatus(Integer id, Participant.DepositStatus depositStatus) {
        Participant participant = getParticipantByIdV2(id);
        participant.setDepositStatus(depositStatus);
        return participantRepository.save(participant);
    }

    @Transactional
    @Override
    public Participant updateAuctionProcessStatus(Integer participantId, AuctionProcessStatus status) {
        Participant participant = getParticipantByIdV2(participantId);
        participant.setAuctionProcessStatus(status);
        if (status.equals(AuctionProcessStatus.PAYMENT_COMPLETED)){
            participant.setDepositStatus(Participant.DepositStatus.WINNER);
        }
        return participantRepository.save(participant);
    }

    @Override
    public Participant getParticipantByAuctionIdAndUserId(Integer auctionId, Integer userId) {
        return participantRepository.findByAuctionIdAndUserId(auctionId, userId).orElseThrow(
                () -> new ResourceNotFoundException(PARTICIPANT, "auctionId/userId", auctionId + "/" +userId)
        );
    }

    @Transactional
    @Override
    public void updateHighestBid(Integer id, Long finalBid) {
        Participant participant = getParticipantByIdV2(id);
        participant.setHighestBid(finalBid);
    }

    @Override
    public List<ParticipantResponse> getParticipantResponseByAuctionId(Integer auctionId) {
        return participantRepository.findAllByAuctionId(auctionId).stream().sorted((p1, p2) -> Long.compare(p2.getHighestBid(), p1.getHighestBid())).map(ParticipantMapper::mapToAuctionParticipantResponse).toList();
    }

    @Override
    @Transactional
    public ParticipantResponse sendConfirmationToParticipant(Integer auctionId) {
        Auction auction = auctionService.getAuctionById(auctionId);
        if (auction.getStartDate().isAfter(Instant.now())){
            throw new InvalidRequestException("The auction has not started yet", HttpStatus.BAD_REQUEST);
        } else if (auction.getEndDate().isAfter(Instant.now())) {
            throw new InvalidRequestException("The auction is still ongoing", HttpStatus.BAD_REQUEST);
        }
        Participant participant = participantRepository.findWinnerByAuctionId(auctionId).orElseThrow(
                () -> new ResourceNotFoundException(PARTICIPANT, "auctionId", auctionId)
        );
        Item item = participant.getAuction().getItem();
        if (participant.getAuctionProcessStatus() != null){
            throw new InvalidRequestException("Confirmation has already sent to this user", HttpStatus.CONFLICT);
        }
        MessageTemplate messageTemplate = MessageTemplate.builder()
                .name("auctionWinnerConfirmation")
                .data(Map.of(
                        "item_name", item.getTitle(),
                            "winning_bid", participant.getHighestBid()
                )).build();
        notificationService.sendNotification(NotificationChannel.EMAIL, "Auction Winner Confirmation", messageTemplate, participant.getUser().getEmail());
        participant.setAuctionProcessStatus(AuctionProcessStatus.PENDING_CONFIRMATION);
        participant.setConfirmationExpiry(Instant.now().plus(24, ChronoUnit.HOURS));
        return ParticipantMapper.mapToAuctionParticipantResponse(participantRepository.save(participant));
    }

    @Override
    public ParticipantResponse confirmTheAuction(Integer participantId){
        Participant participant = getParticipantByIdV2(participantId);
        AuctionProcessStatus status = participant.getAuctionProcessStatus();
        boolean isPendingConfirmation = status.equals(AuctionProcessStatus.PENDING_CONFIRMATION);
        if (!isPendingConfirmation){
            throw new InvalidRequestException("Auction process cannot be confirmed", HttpStatus.BAD_REQUEST);
        }
        participant.setAuctionProcessStatus(AuctionProcessStatus.PAYMENT_PENDING);
        participant.setPaymentExpiry(Instant.now().plus(24, ChronoUnit.HOURS));
        return ParticipantMapper.mapToAuctionParticipantResponse(participantRepository.save(participant));
    }

    @Override
    public ParticipantResponse cancelAuctionProcess(Integer participantId){
        Participant participant = getParticipantByIdV2(participantId);
        boolean isPendingConfirm = participant.getAuctionProcessStatus().equals(AuctionProcessStatus.PENDING_CONFIRMATION);
        boolean isPendingPayment = participant.getAuctionProcessStatus().equals(AuctionProcessStatus.PAYMENT_PENDING);
        if (!(isPendingConfirm || isPendingPayment)){
            throw new InvalidRequestException("Auction process cannot be canceled", HttpStatus.BAD_REQUEST);
        }

        if (isPendingConfirm){
            participant.setAuctionProcessStatus(AuctionProcessStatus.CONFIRMATION_CANCELED);
        } else {
            participant.setAuctionProcessStatus(AuctionProcessStatus.PAYMENT_CANCELED);
        }

        participant.setDepositStatus(Participant.DepositStatus.PENALIZED);
        return ParticipantMapper.mapToAuctionParticipantResponse(participantRepository.save(participant));
    }

    @Override
    public ParticipantResponse setPenalty(Integer participantId){
        Participant participant = getParticipantByIdV2(participantId);
        Integer auctionId = participant.getAuction().getId();
        AuctionProcessStatus status = participant.getAuctionProcessStatus();
        boolean isPendingConfirmation = status.equals(AuctionProcessStatus.PENDING_CONFIRMATION);
        boolean isPendingPayment = status.equals(AuctionProcessStatus.PAYMENT_PENDING);
        boolean isConfirmationExpired = participant.getConfirmationExpiry().isBefore(Instant.now());
        boolean isPaymentExpired = participant.getPaymentExpiry().isBefore(Instant.now());

        if ((!isPendingConfirmation && !isPendingPayment) ||
                (isPendingConfirmation && !isConfirmationExpired) ||
                (isPendingPayment && !isPaymentExpired)
        ){
            throw new InvalidRequestException("You can't set penalty to this user", HttpStatus.BAD_REQUEST);
        }

        if (isPendingConfirmation){
            List<Participant> participants = getParticipantByAuctionId(auctionId).stream().sorted((p1, p2) -> Long.compare(p2.getHighestBid(), p1.getHighestBid())).toList();
            Integer firstWinnerId = participants.get(0).getId();
            if (firstWinnerId.equals(participantId)){
                participant.setDepositStatus(Participant.DepositStatus.PENALIZED);
            }
            participant.setAuctionProcessStatus(AuctionProcessStatus.CONFIRMATION_CANCELED);
        } else {
            participant.setAuctionProcessStatus(AuctionProcessStatus.PAYMENT_CANCELED);
            participant.setDepositStatus(Participant.DepositStatus.PENALIZED);
        }
        return ParticipantMapper.mapToAuctionParticipantResponse(participantRepository.save(participant));
    }


}
