package com.car.foryou.service.participant;

import com.car.foryou.dto.participant.ParticipantResponse;
import com.car.foryou.exception.InvalidRequestException;
import com.car.foryou.exception.ResourceAlreadyExistsException;
import com.car.foryou.exception.ResourceNotFoundException;
import com.car.foryou.mapper.ParticipantMapper;
import com.car.foryou.model.Auction;
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
                .depositStatus(Participant.DepositStatus.UNPAID)
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
    public Participant updateAuctionProcessStatus(Integer participantId, Participant.AuctionProcessStatus status) {
        Participant participant = getParticipantByIdV2(participantId);
        participant.setAuctionProcessStatus(status);
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


//    public List<Participant> getAllParticipants(){
//        return auctionParticipantRepository.findAll();
//    }
//
//    public Participant getAuctionParticipantById(Integer id){
//        return auctionParticipantRepository.findById(id).orElseThrow(
//                () -> new ResourceNotFoundException(AUCTION_PARTICIPANT,"ID", id)
//        );
//    }
//
//    public Participant getParticipantByItemIdAndUserId(Integer itemId, Integer userId){
//        return auctionParticipantRepository.findByItemIdAndParticipantId(itemId, userId).orElseThrow(
//                () -> new ResourceNotFoundException(AUCTION_PARTICIPANT, "Item And User", itemId)
//        );
//    }
//
//    public List<Participant> getParticipantByItemId(Integer itemId){
//        return auctionParticipantRepository.findByItemId(itemId);
//    }
//
//    @Override
//    public List<ParticipantResponse> getAllAuctionParticipants() {
//        List<Participant> participants = getAllParticipants();
//        return participants.stream().map(ParticipantMapper::mapToAuctionParticipantResponse).toList();
//    }
//
//    @Transactional
//    @Override
//    public GeneralResponse<ParticipantResponse> register(Integer itemId, AuctionParticipantRequest request) {
//        try {
//            Item item = itemService.getItemById(itemId);
//            User user = getCurrentUser();
//            validateAuctionRegistration(request, item);
//            Participant participant = prepareParticipant(item.getId(), user, request);
//            Participant saved = auctionParticipantRepository.save(participant);
//            ParticipantResponse response = ParticipantMapper.mapToAuctionParticipantResponse(saved);
//            MessageTemplate message = createRegistrationTemplateMessage(response, item);
//            notificationService.sendNotification(NotificationChannel.EMAIL, "Auction Registration", message, user.getEmail());
//            return GeneralResponse.<ParticipantResponse>builder()
//                    .message("You have successfully registered for the auction")
//                    .data(response)
//                    .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
//                    .build();
//        }catch (InvalidRequestException e){
//            throw new GeneralException(e.getMessage(), e.getStatus());
//        }
//    }
//
//    private MessageTemplate createRegistrationTemplateMessage(ParticipantResponse participantResponse, Item item){
//        return MessageTemplate.builder()
//                .name("auctionRegistrationSuccess")
//                .data(Map.of("deposit_amount", participantResponse.getDepositAmount(),
//                        "auction_start_date", item.getAuctionStart(),
//                        "auction_end_date", item.getAuctionEnd(),
//                        "starting_price", item.getStartingPrice(),
//                        "item_name", item.getTitle(),
//                        "auction_id", item.getId()))
//                .build();
//    }
//
//    private User getCurrentUser(){
//        return User.builder()
//                .id(CustomUserDetailService.getLoggedInUserDetails().getId())
//                .email(CustomUserDetailService.getLoggedInUserDetails().getEmail())
//                .build();
//    }
//
//    private Participant prepareParticipant(Integer itemId, User user, AuctionParticipantRequest request){
//        return Participant.builder()
//                .itemId(itemId)
//                .participant(user)
//                .depositAmount(request.getDepositAmount())
//                .paymentMethod(request.getPaymentMethod())
//                .registrationStatus(AuctionRegistrationStatus.REGISTERED)
//                .build();
//    }
//
//    @Override
//    public GeneralResponse<ParticipantResponse> cancelRegistration(Integer itemId, AuthParticipantCancelRequest request) {
//        Item item = itemService.getItemById(itemId);
//        Integer userId = CustomUserDetailService.getLoggedInUserDetails().getId();
//        Participant participant = getParticipantByItemIdAndUserId(itemId, userId);
//        if (Instant.now().isAfter(item.getAuctionStart().toInstant().minusSeconds(60L * 60L * 2L))){
//            throw new InvalidRequestException("You can't cancel your registration, the auction is about to start", HttpStatus.BAD_REQUEST);
//        }
//        participant.setRegistrationStatus(AuctionRegistrationStatus.CANCELLED);
//        participant.setCancelReason(request.reason());
//        Participant saved = auctionParticipantRepository.save(participant);
//        ParticipantResponse response = ParticipantMapper.mapToAuctionParticipantResponse(saved);
//        MessageTemplate message = createCancelRegistration(response, item);
//        notificationService.sendNotification(NotificationChannel.EMAIL, "Auction Registration Cancellation", message, item.getAuctioneer().getEmail());
//        return GeneralResponse.<ParticipantResponse>builder()
//                .message("You have successfully cancelled your registration, your deposit will be returned in 3 to 5 working days")
//                .data(response)
//                .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
//                .build();
//    }
//
//    private MessageTemplate createCancelRegistration(ParticipantResponse participantResponse, Item item){
//        return MessageTemplate.builder()
//                .name("auctionCancelReq")
//                .data(Map.of("deposit_amount", participantResponse.getDepositAmount(),
//                        "auction_start_date", item.getAuctionStart(),
//                        "auction_end_date", item.getAuctionEnd(),
//                        "starting_price", item.getStartingPrice(),
//                        "item_name", item.getTitle(),
//                        "item_id", item.getId(),
//                        "registration_id", participantResponse.getRegistrationId()))
//                .build();
//    }
//
//    @Override
//    @Transactional
//    public GeneralResponse<ParticipantResponse> refundDeposit(Integer registrationId) {
//        Participant participant = getAuctionParticipantById(registrationId);
//        Integer itemId = participant.getItemId();
//        ItemResponse itemById = itemService.getItemResponseById(itemId);
//        if (!Objects.equals(itemById.getAuctioneer(), CustomUserDetailService.getLoggedInUserDetails().getUsername())){
//            throw new InvalidRequestException("You can't refund the deposit, you are not the auctioneer", HttpStatus.BAD_REQUEST);
//        }
//        participant.setRegistrationStatus(AuctionRegistrationStatus.REFUNDED);
//        Participant saved = auctionParticipantRepository.save(participant);
//        ParticipantResponse response = ParticipantMapper.mapToAuctionParticipantResponse(saved);
//        MessageTemplate message = MessageTemplate.builder()
//                .name("refunded")
//                .data(Map.of("amount", saved.getDepositAmount(),
//                        "product", "Auction"))
//                .build();
//        String depositRefund = notificationService.sendNotification(NotificationChannel.EMAIL, "Deposit Refund", message, saved.getParticipant().getEmail());
//        return GeneralResponse.<ParticipantResponse>builder()
//                .message(depositRefund)
//                .data(response)
//                .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
//                .build();
//
//    }
//
//    @Override
//    public ParticipantResponse getParticipantResponseByItemIdAndUserId(Integer itemId, Integer userId) {
//        Participant participant = getParticipantByItemIdAndUserId(itemId, userId);
//        return ParticipantMapper.mapToAuctionParticipantResponse(participant);
//    }
//
//    @Override
//    public void setPenalty(Integer itemId, Integer userId) {
//        Participant participant = getParticipantByItemIdAndUserId(itemId, userId);
//        participant.setRegistrationStatus(AuctionRegistrationStatus.PENALTY);
//        auctionParticipantRepository.save(participant);
//    }
//
//    @Transactional
//    @Override
//    public GeneralResponse<ParticipantResponse> bulkRefundDeposit(Integer itemId) {
//        List<Participant> participants = auctionParticipantRepository.findByItemId(itemId);
//        List<Participant> refundableParticipants = participants.stream().filter(participant -> participant.getRegistrationStatus().equals(AuctionRegistrationStatus.REGISTERED)).toList();
//        Set<String> recipients = refundableParticipants.stream().map(participant -> participant.getParticipant().getEmail()).collect(Collectors.toUnmodifiableSet());
//        refundableParticipants.forEach(participant -> participant.setRegistrationStatus(AuctionRegistrationStatus.REFUNDED));
//        auctionParticipantRepository.saveAll(refundableParticipants);
//        MessageTemplate message = MessageTemplate.builder()
//                .name("refunded")
//                .data(Map.of("amount", refundableParticipants.get(0).getDepositAmount(),
//                        "product", "Auction"))
//                .build();
//        notificationService.sendBatchNotification(NotificationChannel.EMAIL, "Deposit Refund", message, recipients);
//        return GeneralResponse.<ParticipantResponse>builder()
//                .message("Deposits refunded successfully")
//                .data(null)
//                .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
//                .build();
//    }
//
//    @Transactional
//    @Override
//    public void setWinner(Integer userId, Integer itemId) {
//        ItemResponse itemById = itemService.getItemResponseById(itemId);
//        UserResponse userById = userService.getUserResponseById(userId);
//        Participant participant = getParticipantByItemIdAndUserId(itemById.getItemId(), userById.getId());
//        if (!participant.getRegistrationStatus().equals(AuctionRegistrationStatus.REGISTERED)){
//            throw new InvalidRequestException("User is not registered for the auction", HttpStatus.BAD_REQUEST);
//        }
//        participant.setRegistrationStatus(AuctionRegistrationStatus.WINNER);
//        auctionParticipantRepository.save(participant);
//    }
//
//    @Override
//    public ParticipantResponse getParticipantResponseById(Integer participantId) {
//        Participant participant = getAuctionParticipantById(participantId);
//        return ParticipantMapper.mapToAuctionParticipantResponse(participant);
//    }
//
//    @Override
//    public List<ParticipantResponse> getParticipantResponseByItemId(Integer itemId) {
//        List<Participant> participant = getParticipantByItemId(itemId);
//        return participant.stream().map(ParticipantMapper::mapToAuctionParticipantResponse).toList();
//    }
//
//    @Override
//    public ParticipantResponse updateParticipant(AuctionParticipantUpdateRequest request) {
//        Participant foundedParticipant = getParticipantById(request.getId());
//        foundedParticipant.setRegistrationStatus(request.getStatus());
//        return ParticipantMapper.mapToAuctionParticipantResponse(foundedParticipant);
//    }
//
//    private Participant getParticipantById(Integer id){
//        return auctionParticipantRepository.findById(id).orElseThrow(
//                () -> new ResourceNotFoundException(AUCTION_PARTICIPANT, "ID", id)
//        );
//    }
//
//    private void validateAuctionRegistration(AuctionParticipantRequest request, Item item) {
//        if (request.getDepositAmount() == null || request.getPaymentMethod() == null){
//            throw new InvalidRequestException("Deposit amount and payment method are required", HttpStatus.BAD_REQUEST);
//        }
//
//        if (!item.getStatus().equals(ItemStatus.AUCTION_SCHEDULED)){
//            throw new InvalidRequestException("You can't register for the auction, the auction is not scheduled", HttpStatus.BAD_REQUEST);
//        }
//        if (Instant.now().isAfter(item.getAuctionStart().toInstant().minusSeconds(60L * 60L))){
//            throw new InvalidRequestException("You can't register for the auction, the registration time has passed", HttpStatus.BAD_REQUEST);
//        }
//
//        Integer userId = CustomUserDetailService.getLoggedInUserDetails().getId();
//        Optional<Participant> foundedParticipant = auctionParticipantRepository.findByItemIdAndParticipantId(item.getId(), userId);
//        if (foundedParticipant.isPresent() && foundedParticipant.get().getRegistrationStatus().equals(AuctionRegistrationStatus.REGISTERED)){
//                throw new ResourceAlreadyExistsException("Participant", HttpStatus.CONFLICT);
//            }
//
//        if (request.getPaymentMethod().equals(PaymentMethod.BANK_TRANSFER)){
//            if (request.getDepositAmount() != 10000000){
//                throw new InvalidRequestException("Deposit amount incorrect, your deposit will be returned", HttpStatus.BAD_REQUEST);
//            }
//        }else {
//            request.setDepositAmount(10000000);
//        }
//    }
}
