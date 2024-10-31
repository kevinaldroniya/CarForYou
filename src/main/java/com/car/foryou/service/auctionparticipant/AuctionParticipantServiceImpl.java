package com.car.foryou.service.auctionparticipant;

import com.car.foryou.dto.GeneralResponse;
import com.car.foryou.dto.auctionparticipant.*;
import com.car.foryou.dto.item.ItemResponse;
import com.car.foryou.dto.item.ItemStatus;
import com.car.foryou.dto.notification.MessageTemplate;
import com.car.foryou.dto.notification.NotificationChannel;
import com.car.foryou.dto.payment.PaymentMethod;
import com.car.foryou.dto.user.UserResponse;
import com.car.foryou.exception.GeneralException;
import com.car.foryou.exception.InvalidRequestException;
import com.car.foryou.exception.ResourceAlreadyExistsException;
import com.car.foryou.exception.ResourceNotFoundException;
import com.car.foryou.mapper.AuctionParticipantMapper;
import com.car.foryou.model.AuctionParticipant;
import com.car.foryou.model.Item;
import com.car.foryou.model.User;
import com.car.foryou.repository.auctionparticipant.AuctionParticipantRepository;
import com.car.foryou.service.notification.NotificationService;
import com.car.foryou.service.user.CustomUserDetailService;
import com.car.foryou.service.item.ItemService;
import com.car.foryou.service.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuctionParticipantServiceImpl implements AuctionParticipantService {
    private final AuctionParticipantRepository auctionParticipantRepository;
    private final ItemService itemService;
    private final NotificationService notificationService;
    private final UserService userService;

    private static final String AUCTION_PARTICIPANT = "AUCTION_PARTICIPANT";

    public AuctionParticipantServiceImpl(AuctionParticipantRepository auctionParticipantRepository, ItemService itemService, NotificationService notificationService, UserService userService) {
        this.auctionParticipantRepository = auctionParticipantRepository;
        this.itemService = itemService;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    public List<AuctionParticipant> getAllParticipants(){
        return auctionParticipantRepository.findAll();
    }

    public AuctionParticipant getAuctionParticipantById(Integer id){
        return auctionParticipantRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(AUCTION_PARTICIPANT,"ID", id)
        );
    }

    public AuctionParticipant getParticipantByItemIdAndUserId(Integer itemId, Integer userId){
        return auctionParticipantRepository.findByItemIdAndParticipantId(itemId, userId).orElseThrow(
                () -> new ResourceNotFoundException(AUCTION_PARTICIPANT, "Item And User", itemId)
        );
    }

    public List<AuctionParticipant> getParticipantByItemId(Integer itemId){
        return auctionParticipantRepository.findByItemId(itemId);
    }

    @Override
    public List<AuctionParticipantResponse> getAllAuctionParticipants() {
        List<AuctionParticipant> auctionParticipants = getAllParticipants();
        return auctionParticipants.stream().map(AuctionParticipantMapper::mapToAuctionParticipantResponse).toList();
    }

    @Transactional
    @Override
    public GeneralResponse<AuctionParticipantResponse> register(Integer itemId, AuctionParticipantRequest request) {
        try {
            Item item = itemService.getItemById(itemId);
            User user = getCurrentUser();
            validateAuctionRegistration(request, item);
            AuctionParticipant auctionParticipant = prepareParticipant(item.getId(), user, request);
            AuctionParticipant saved = auctionParticipantRepository.save(auctionParticipant);
            AuctionParticipantResponse response = AuctionParticipantMapper.mapToAuctionParticipantResponse(saved);
            MessageTemplate message = createRegistrationTemplateMessage(response, item);
            notificationService.sendNotification(NotificationChannel.EMAIL, "Auction Registration", message, user.getEmail());
            return GeneralResponse.<AuctionParticipantResponse>builder()
                    .message("You have successfully registered for the auction")
                    .data(response)
                    .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
                    .build();
        }catch (InvalidRequestException e){
            throw new GeneralException(e.getMessage(), e.getStatus());
        }
    }

    private MessageTemplate createRegistrationTemplateMessage(AuctionParticipantResponse participantResponse, Item item){
        return MessageTemplate.builder()
                .name("auctionRegistrationSuccess")
                .data(Map.of("deposit_amount", participantResponse.getDepositAmount(),
                        "auction_start_date", item.getAuctionStart(),
                        "auction_end_date", item.getAuctionEnd(),
                        "starting_price", item.getStartingPrice(),
                        "item_name", item.getTitle(),
                        "auction_id", item.getId()))
                .build();
    }

    private User getCurrentUser(){
        return User.builder()
                .id(CustomUserDetailService.getLoggedInUserDetails().getId())
                .email(CustomUserDetailService.getLoggedInUserDetails().getEmail())
                .build();
    }

    private AuctionParticipant prepareParticipant(Integer itemId, User user, AuctionParticipantRequest request){
        return AuctionParticipant.builder()
                .itemId(itemId)
                .participant(user)
                .depositAmount(request.getDepositAmount())
                .paymentMethod(request.getPaymentMethod())
                .registrationStatus(AuctionRegistrationStatus.REGISTERED)
                .build();
    }

    @Override
    public GeneralResponse<AuctionParticipantResponse> cancelRegistration(Integer itemId, AuthParticipantCancelRequest request) {
        Item item = itemService.getItemById(itemId);
        Integer userId = CustomUserDetailService.getLoggedInUserDetails().getId();
        AuctionParticipant participant = getParticipantByItemIdAndUserId(itemId, userId);
        if (Instant.now().isAfter(item.getAuctionStart().toInstant().minusSeconds(60L * 60L * 2L))){
            throw new InvalidRequestException("You can't cancel your registration, the auction is about to start", HttpStatus.BAD_REQUEST);
        }
        participant.setRegistrationStatus(AuctionRegistrationStatus.CANCELLED);
        participant.setCancelReason(request.reason());
        AuctionParticipant saved = auctionParticipantRepository.save(participant);
        AuctionParticipantResponse response = AuctionParticipantMapper.mapToAuctionParticipantResponse(saved);
        MessageTemplate message = createCancelRegistration(response, item);
        notificationService.sendNotification(NotificationChannel.EMAIL, "Auction Registration Cancellation", message, item.getAuctioneer().getEmail());
        return GeneralResponse.<AuctionParticipantResponse>builder()
                .message("You have successfully cancelled your registration, your deposit will be returned in 3 to 5 working days")
                .data(response)
                .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
                .build();
    }

    private MessageTemplate createCancelRegistration(AuctionParticipantResponse participantResponse, Item item){
        return MessageTemplate.builder()
                .name("auctionCancelReq")
                .data(Map.of("deposit_amount", participantResponse.getDepositAmount(),
                        "auction_start_date", item.getAuctionStart(),
                        "auction_end_date", item.getAuctionEnd(),
                        "starting_price", item.getStartingPrice(),
                        "item_name", item.getTitle(),
                        "item_id", item.getId(),
                        "registration_id", participantResponse.getRegistrationId()))
                .build();
    }

    @Override
    @Transactional
    public GeneralResponse<AuctionParticipantResponse> refundDeposit(Integer registrationId) {
        AuctionParticipant participant = getAuctionParticipantById(registrationId);
        Integer itemId = participant.getItemId();
        ItemResponse itemById = itemService.getItemResponseById(itemId);
        if (!Objects.equals(itemById.getAuctioneer(), CustomUserDetailService.getLoggedInUserDetails().getUsername())){
            throw new InvalidRequestException("You can't refund the deposit, you are not the auctioneer", HttpStatus.BAD_REQUEST);
        }
        participant.setRegistrationStatus(AuctionRegistrationStatus.REFUNDED);
        AuctionParticipant saved = auctionParticipantRepository.save(participant);
        AuctionParticipantResponse response = AuctionParticipantMapper.mapToAuctionParticipantResponse(saved);
        MessageTemplate message = MessageTemplate.builder()
                .name("refunded")
                .data(Map.of("amount", saved.getDepositAmount(),
                        "product", "Auction"))
                .build();
        String depositRefund = notificationService.sendNotification(NotificationChannel.EMAIL, "Deposit Refund", message, saved.getParticipant().getEmail());
        return GeneralResponse.<AuctionParticipantResponse>builder()
                .message(depositRefund)
                .data(response)
                .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
                .build();

    }

    @Override
    public AuctionParticipantResponse getParticipantResponseByItemIdAndUserId(Integer itemId, Integer userId) {
        AuctionParticipant auctionParticipant = getParticipantByItemIdAndUserId(itemId, userId);
        return AuctionParticipantMapper.mapToAuctionParticipantResponse(auctionParticipant);
    }

    @Override
    public void setPenalty(Integer itemId, Integer userId) {
        AuctionParticipant auctionParticipant = getParticipantByItemIdAndUserId(itemId, userId);
        auctionParticipant.setRegistrationStatus(AuctionRegistrationStatus.PENALTY);
        auctionParticipantRepository.save(auctionParticipant);
    }

    @Transactional
    @Override
    public GeneralResponse<AuctionParticipantResponse> bulkRefundDeposit(Integer itemId) {
        List<AuctionParticipant> participants = auctionParticipantRepository.findByItemId(itemId);
        List<AuctionParticipant> refundableParticipants = participants.stream().filter(auctionParticipant -> auctionParticipant.getRegistrationStatus().equals(AuctionRegistrationStatus.REGISTERED)).toList();
        Set<String> recipients = refundableParticipants.stream().map(auctionParticipant -> auctionParticipant.getParticipant().getEmail()).collect(Collectors.toUnmodifiableSet());
        refundableParticipants.forEach(auctionParticipant -> auctionParticipant.setRegistrationStatus(AuctionRegistrationStatus.REFUNDED));
        auctionParticipantRepository.saveAll(refundableParticipants);
        MessageTemplate message = MessageTemplate.builder()
                .name("refunded")
                .data(Map.of("amount", refundableParticipants.get(0).getDepositAmount(),
                        "product", "Auction"))
                .build();
        notificationService.sendBatchNotification(NotificationChannel.EMAIL, "Deposit Refund", message, recipients);
        return GeneralResponse.<AuctionParticipantResponse>builder()
                .message("Deposits refunded successfully")
                .data(null)
                .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
                .build();
    }

    @Transactional
    @Override
    public void setWinner(Integer userId, Integer itemId) {
        ItemResponse itemById = itemService.getItemResponseById(itemId);
        UserResponse userById = userService.getUserResponseById(userId);
        AuctionParticipant auctionParticipant = getParticipantByItemIdAndUserId(itemById.getItemId(), userById.getId());
        if (!auctionParticipant.getRegistrationStatus().equals(AuctionRegistrationStatus.REGISTERED)){
            throw new InvalidRequestException("User is not registered for the auction", HttpStatus.BAD_REQUEST);
        }
        auctionParticipant.setRegistrationStatus(AuctionRegistrationStatus.WINNER);
        auctionParticipantRepository.save(auctionParticipant);
    }

    @Override
    public AuctionParticipantResponse getParticipantResponseById(Integer participantId) {
        AuctionParticipant auctionParticipant = getAuctionParticipantById(participantId);
        return AuctionParticipantMapper.mapToAuctionParticipantResponse(auctionParticipant);
    }

    @Override
    public List<AuctionParticipantResponse> getParticipantResponseByItemId(Integer itemId) {
        List<AuctionParticipant> participant = getParticipantByItemId(itemId);
        return participant.stream().map(AuctionParticipantMapper::mapToAuctionParticipantResponse).toList();
    }

    @Override
    public AuctionParticipantResponse updateParticipant(AuctionParticipantUpdateRequest request) {
        AuctionParticipant foundedParticipant = getParticipantById(request.getId());
        foundedParticipant.setRegistrationStatus(request.getStatus());
        return AuctionParticipantMapper.mapToAuctionParticipantResponse(foundedParticipant);
    }

    private AuctionParticipant getParticipantById(Integer id){
        return auctionParticipantRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(AUCTION_PARTICIPANT, "ID", id)
        );
    }

    private void validateAuctionRegistration(AuctionParticipantRequest request, Item item) {
        if (request.getDepositAmount() == null || request.getPaymentMethod() == null){
            throw new InvalidRequestException("Deposit amount and payment method are required", HttpStatus.BAD_REQUEST);
        }

        if (!item.getStatus().equals(ItemStatus.AUCTION_SCHEDULED)){
            throw new InvalidRequestException("You can't register for the auction, the auction is not scheduled", HttpStatus.BAD_REQUEST);
        }
        if (Instant.now().isAfter(item.getAuctionStart().toInstant().minusSeconds(60L * 60L))){
            throw new InvalidRequestException("You can't register for the auction, the registration time has passed", HttpStatus.BAD_REQUEST);
        }

        Integer userId = CustomUserDetailService.getLoggedInUserDetails().getId();
        Optional<AuctionParticipant> foundedParticipant = auctionParticipantRepository.findByItemIdAndParticipantId(item.getId(), userId);
        if (foundedParticipant.isPresent() && foundedParticipant.get().getRegistrationStatus().equals(AuctionRegistrationStatus.REGISTERED)){
                throw new ResourceAlreadyExistsException("AuctionParticipant", HttpStatus.CONFLICT);
            }

        if (request.getPaymentMethod().equals(PaymentMethod.BANK_TRANSFER)){
            if (request.getDepositAmount() != 10000000){
                throw new InvalidRequestException("Deposit amount incorrect, your deposit will be returned", HttpStatus.BAD_REQUEST);
            }
        }else {
            request.setDepositAmount(10000000);
        }
    }
}
