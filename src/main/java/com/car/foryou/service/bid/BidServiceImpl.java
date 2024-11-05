package com.car.foryou.service.bid;

import com.car.foryou.dto.GeneralResponse;
import com.car.foryou.dto.auctionparticipant.AuctionParticipantResponse;
import com.car.foryou.dto.auctionparticipant.AuctionRegistrationStatus;
import com.car.foryou.dto.bid.BidConfirmationRequest;
import com.car.foryou.dto.bid.BidDetailResponse;
import com.car.foryou.dto.bid.BidStatus;
import com.car.foryou.dto.bid.BidUpdateRequest;
import com.car.foryou.dto.item.ItemResponse;
import com.car.foryou.dto.item.ItemStatus;
import com.car.foryou.dto.notification.MessageTemplate;
import com.car.foryou.dto.notification.NotificationChannel;
import com.car.foryou.dto.otp.OtpType;
import com.car.foryou.dto.payment.PaymentConfirmationRequest;
import com.car.foryou.dto.payment.PaymentResponse;
import com.car.foryou.dto.payment.PaymentSetRequest;
import com.car.foryou.dto.payment.PaymentStatus;
import com.car.foryou.dto.user.UserResponse;
import com.car.foryou.exception.*;
import com.car.foryou.helper.EncryptionHelper;
import com.car.foryou.mapper.BidDetailMapper;
import com.car.foryou.model.BidDetail;
import com.car.foryou.model.Otp;
import com.car.foryou.model.User;
import com.car.foryou.repository.bid.BidDetailRepository;
import com.car.foryou.service.auctionparticipant.AuctionParticipantService;
import com.car.foryou.service.item.ItemService;
import com.car.foryou.service.notification.NotificationService;
import com.car.foryou.service.otp.OtpService;
import com.car.foryou.service.payment.PaymentService;
import com.car.foryou.service.user.CustomUserDetailService;
import com.car.foryou.service.user.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;


@Service
public class BidServiceImpl implements BidService{

    private final BidDetailRepository bidDetailRepository;
    private final ItemService itemService;
    private final AuctionParticipantService auctionParticipantService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final OtpService otpService;
    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;
    private final EncryptionHelper encryptionHelper;

    private static final String NON_AUCTION_ITEM = "Item is not on auction, or auction has ended";
    private static final String SUCCESSFUL_BID = "Bid placed successfully";
    private static final String BID_DETAIL = "BidDetail";
    private static final String ID = "ID";
    private static final long BID_INCREMENT = 500_000;
    private static final String CAN_NOT_SET_PENALTY = "You can't set penalty to this user";
    private static final String CAN_NOT_SENT_WINNER_CONFIRMATION = "You can't sent winner confirmation to this user";

    public BidServiceImpl(BidDetailRepository bidDetailRepository, ItemService itemService, AuctionParticipantService auctionParticipantService, UserService userService, NotificationService notificationService, OtpService otpService, PaymentService paymentService, ObjectMapper objectMapper, EncryptionHelper encryptionHelper) {
        this.bidDetailRepository = bidDetailRepository;
        this.itemService = itemService;
        this.auctionParticipantService = auctionParticipantService;
        this.userService = userService;
        this.notificationService = notificationService;
        this.otpService = otpService;
        this.paymentService = paymentService;
        this.objectMapper = objectMapper;
        this.encryptionHelper = encryptionHelper;
    }

    @Override
    @Transactional
    public GeneralResponse<String> placeBid(Integer itemId) {
        ItemResponse itemById = itemService.getItemResponseById(itemId);
        AuctionParticipantResponse participant = auctionParticipantService.getParticipantResponseByItemIdAndUserId(itemById.getItemId(), CustomUserDetailService.getLoggedInUserDetails().getId());
        if (!itemById.getStatus().equals(ItemStatus.ON_AUCTION) || itemById.getAuctionEnd().isBefore(ZonedDateTime.now())){
            throw new InvalidRequestException(NON_AUCTION_ITEM, HttpStatus.BAD_REQUEST);
        }
        if (!participant.getRegistrationStatus().equals(AuctionRegistrationStatus.REGISTERED)){
            throw new InvalidRequestException("You are not registered for this auction", HttpStatus.BAD_REQUEST);
        }
        Long currentBid;
        Optional<BidDetail> currentTopBid = bidDetailRepository.findByItemIdOrderByTotalBidDesc(itemId).stream().findFirst();
        if (currentTopBid.isPresent()){
            currentBid = currentTopBid.get().getTotalBid();
        }else {
            currentBid = itemById.getStartingPrice();
        }
        UserResponse userFounded = userService.getUserResponseByEmailOrUsernameOrPhoneNumber(participant.getUsername());
        User user = User.builder()
                .id(userFounded.getId())
                .build();
        BidDetail bidDetail = BidDetail.builder()
                .bidder(user)
                .itemId(itemId)
                .totalBid(currentBid + BID_INCREMENT)
                .status(BidStatus.PLACED)
                .build();
        bidDetailRepository.save(bidDetail);
        return GeneralResponse.<String>builder()
                .message(SUCCESSFUL_BID)
                .data(null)
                .timestamp(ZonedDateTime.now(ZoneId.of("UTC"))).build();
    }

    @Override
    public List<BidDetailResponse> getAllBidByItem(Integer itemId) {
        ItemResponse itemById = itemService.getItemResponseById(itemId);
        List<BidDetail> bidDetails = bidDetailRepository.findAllByItemId(itemById.getItemId());
        return bidDetails.stream().sorted((bid1, bid2) -> Long.compare(bid2.getTotalBid(), bid1.getTotalBid())).map(BidDetailMapper::toBidDetailResponse).toList();
    }

    @Override
    public BidDetailResponse getBidDetailResponseById(Integer bidId) {
        Integer userReqId = CustomUserDetailService.getLoggedInUserDetails().getId();
        BidDetail bidDetail = getBidDetailById(bidId);
        if (!bidDetail.getBidder().getId().equals(userReqId)){
            throw new InvalidRequestException("You are not the owner of this bid", HttpStatus.BAD_REQUEST);
        }
        return BidDetailMapper.toBidDetailResponse(bidDetail);
    }

    @Override
    public BidDetailResponse updateBidDetail(BidUpdateRequest request) {
        BidDetail foundedBid = getBidDetailById(request.getBidId());
        foundedBid.setStatus(request.getBidStatus());
        return BidDetailMapper.toBidDetailResponse(bidDetailRepository.save(foundedBid));
    }

    @Override
    public List<Long> getHighestBid(Integer itemId) {
        List<BidDetail> bidDetails = bidDetailRepository.findAllByItemId(itemId);
        // get top 3 highest bid
        return bidDetails.stream().sorted((bid1, bid2) -> Long.compare(bid2.getTotalBid(), bid1.getTotalBid())).limit(3).map(BidDetail::getTotalBid).toList();
    }

    @Override
    public List<BidDetailResponse> getAuctionWinner(Integer itemId) {
        ItemResponse item = itemService.getItemResponseById(itemId);
        int foundedItemId = item.getItemId();
        List<BidDetail> bidDetails = bidDetailRepository.findAllByItemId(foundedItemId);
        return bidDetails.stream()
                // get the highest bid
                .sorted((bid1, bid2) -> Long.compare(bid2.getTotalBid(), bid1.getTotalBid()))
                // get the distinct bidder
                .filter(distinctKey(BidDetail::getBidder))
                // map to BidDetailResponse
                .map(BidDetailMapper::toBidDetailResponse)
                .toList();
    }

    @Override
    public GeneralResponse<String> sendWinnerConfirmation(Integer bidDetailId) {
        BidDetail bidDetail = bidDetailRepository.findById(bidDetailId).orElseThrow(
                () -> new ResourceNotFoundException(BID_DETAIL, ID, bidDetailId)
        );
        if (!bidDetail.getStatus().equals(BidStatus.PLACED)){
            throw new InvalidRequestException(CAN_NOT_SENT_WINNER_CONFIRMATION, HttpStatus.BAD_REQUEST);
        }
        List<BidDetailResponse> auctionWinner = this.getAuctionWinner(bidDetail.getItemId());
        int winnerIndex = 0;
        for(BidDetailResponse winner : auctionWinner){

            if (winner.getBidId() == bidDetail.getId()){
                winnerIndex = auctionWinner.indexOf(winner);
                break;
            }
            winnerIndex++;
        }

        if (winnerIndex > 0 && auctionWinner.get(winnerIndex -1).getBidStatus().equals(BidStatus.PLACED)){
            throw new InvalidRequestException(CAN_NOT_SENT_WINNER_CONFIRMATION, HttpStatus.BAD_REQUEST);
        }else if (auctionWinner.get(0).getBidId() != bidDetail.getId()){
            throw new InvalidRequestException(CAN_NOT_SENT_WINNER_CONFIRMATION, HttpStatus.BAD_REQUEST);
        }
//        AuctionParticipantResponse participantResponse = auctionParticipantService.getParticipantResponseByItemIdAndUserId(bidDetail.getItemId(), bidDetail.getBidder().getId());
        ItemResponse item = itemService.getItemResponseById(bidDetail.getItemId());
//        OtpResponse otp = otpService.generateOtp(bidDetail.getBidder().getEmail(), OtpType.WINNER_CONFIRMATION);
//        BidDetailResponse bidDetailResponse = BidDetailMapper.toBidDetailResponse(bidDetail);
//        PaymentConfirmationRequest request = PaymentConfirmationRequest.builder()
//                .bidDetail(bidDetailResponse)
//                .auctionParticipantResponse(participantResponse)
//                .otp(517077)
//                .expirationTime(ZonedDateTime.ofInstant(Instant.ofEpochSecond(1730358263), ZoneId.of("UTC")))
//                .build();
//        try {
//            String stringRequest = objectMapper.writeValueAsString(request);
//            String encrypted = encryptionHelper.encrypt(stringRequest);
//            String winnerConfirmationLink = "http://localhost:8080/bid/confirm?signature=" + encrypted;
            MessageTemplate message = MessageTemplate.builder()
                    .name("auctionWinnerConfirmation")
                    .data(Map.of(
                            "item_name", item.getTitle(),
                            "winning_bid", bidDetail.getTotalBid()
//                            , "confirmation_link", winnerConfirmationLink
                    ))
                    .build();
            bidDetail.setConfirmationExpiredTime(Instant.now().plus(24, ChronoUnit.HOURS));
            bidDetail.setStatus(BidStatus.WAITING_FOR_CONFIRMATION);
            bidDetailRepository.save(bidDetail);
            notificationService.sendNotification(NotificationChannel.EMAIL, "Auction Winner Confirmation", message, bidDetail.getBidder().getEmail());
            return GeneralResponse.<String>builder()
                    .message("Winner confirmation sent successfully")
                    .data(null)
                    .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
                    .build();
//        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | IOException | InvalidKeyException e){
//            throw new GeneralException("There is an issue with our system, please try again, if the issue persists, please contact our support", HttpStatus.INTERNAL_SERVER_ERROR);
//        }

    }

    @Override
    public GeneralResponse<String> confirmBidWinner(String signature) {
        try {
            String decrypted = encryptionHelper.decrypt(signature);
            PaymentConfirmationRequest confirmationRequest = getConfirmationRequest(decrypted);
            BidDetail bidDetail = bidDetailRepository.findById(confirmationRequest.getBidDetail().getBidId()).orElseThrow(
                    () -> new ResourceNotFoundException(BID_DETAIL, ID, confirmationRequest.getBidDetail().getBidId())
            );
            if (!bidDetail.getStatus().equals(BidStatus.WAITING_FOR_CONFIRMATION)){
                throw new InvalidRequestException("You are not the winner of this auction", HttpStatus.BAD_REQUEST);
            }

            User user = userService.getUserByEmailOrUsernameOrPhoneNumber(confirmationRequest.getAuctionParticipantResponse().getUsername());

            if (!bidDetail.getBidder().getEmail().equals(user.getEmail())){
                throw new InvalidRequestException("You are not the winner of this auction", HttpStatus.BAD_REQUEST);
            }
            otpService.unSignOtpVerify(confirmationRequest.getOtp(), bidDetail.getBidder().getEmail());
            bidDetail.setStatus(BidStatus.CONFIRMED);
            bidDetailRepository.save(bidDetail);
            setPaymentDetail(bidDetail.getId());
            return GeneralResponse.<String>builder()
                    .message("You are confirmed as the winner of the auction, you can now proceed to payment, make sure to complete the payment within 24 hours or your deposit will be forfeited")
                    .data(null)
                    .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
                    .build();
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException |
                 InvalidKeyException | InvalidRequestException e){
            throw new GeneralException("There is an issue with your request, please input the correct signature", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public GeneralResponse<String> bidWinnerConfirmation(BidConfirmationRequest request) {
        Set<BidStatus> allowedBidStatus = Set.of(BidStatus.CONFIRMED, BidStatus.CANCELLED_BY_BIDDER);
        if (!allowedBidStatus.contains(request.bidStatus())){
            throw new InvalidRequestException("Invalid BidStatus : " + request.bidStatus(), HttpStatus.BAD_REQUEST);
        }
        GeneralResponse<String> response = GeneralResponse.<String>builder()
                .data(null)
                .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
                .build();
        BidDetail bidDetail = getBidDetailById(request.bidDetailId());
        if (!bidDetail.getStatus().equals(BidStatus.WAITING_FOR_CONFIRMATION) ||
                !bidDetail.getBidder().getId().equals(CustomUserDetailService.getLoggedInUserDetails().getId())){
            throw new InvalidRequestException("You are not the winner of this auction", HttpStatus.BAD_REQUEST);
        }
        if (bidDetail.getConfirmationExpiredTime().isBefore(Instant.now())){
            throw new ResourceExpiredException("BidConfirmation");
        }
        bidDetail.setStatus(request.bidStatus());
        bidDetailRepository.save(bidDetail);

        List<BidDetailResponse> auctionWinner = getAuctionWinner(bidDetail.getItemId());

        if (request.bidStatus().equals(BidStatus.CONFIRMED)){
            setPaymentDetail(bidDetail.getId());
            response.setMessage("You have confirmed your winning bid, please proceed to payment");
        }else if (request.bidStatus().equals(BidStatus.CANCELLED_BY_BIDDER) && auctionWinner.get(0).getBidId() == bidDetail.getId()){
            auctionParticipantService.setPenalty(bidDetail.getItemId(), bidDetail.getBidder().getId());
            setPenalty(bidDetail.getId());
            response.setMessage("You have cancelled your winning bid, your deposit will be forfeited");
        }else{
            response.setMessage("You have cancelled your winning bid");
        }
        return response;
    }

    private PaymentConfirmationRequest getConfirmationRequest(String decodeRequest) {
        try {
            return objectMapper.readValue(decodeRequest, new TypeReference<PaymentConfirmationRequest>() {});
        } catch (JsonProcessingException e) {
            throw new ConversionException("request", "PaymentConfirmationRequest", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @Override
    public GeneralResponse<String> setPenalty(Integer bidDetailId) {
        BidDetail bidDetail = bidDetailRepository.findById(bidDetailId).orElseThrow(
                () -> new ResourceNotFoundException(BID_DETAIL, ID, bidDetailId)
        );

        List<BidDetailResponse> auctionWinner = getAuctionWinner(bidDetail.getItemId());
        if (auctionWinner.get(0).getBidId() != bidDetail.getId()){
            throw new InvalidRequestException(CAN_NOT_SET_PENALTY, HttpStatus.BAD_REQUEST);
        }
        AuctionParticipantResponse participant = auctionParticipantService.getParticipantResponseByItemIdAndUserId(bidDetail.getItemId(), bidDetail.getBidder().getId());
//        Otp foundedOtp = null;
        String bidStatus = "";
        String paymentStatus = "";
        StringBuilder stringBuilder = new StringBuilder();
        if (bidDetail.getStatus().equals(BidStatus.WAITING_FOR_CONFIRMATION)){
//            try {
//                foundedOtp = otpService.getOtpByUserAndOtpType(bidDetail.getBidder().getEmail(), OtpType.WINNER_CONFIRMATION);
//            }catch (ResourceNotFoundException e) {
//                throw new InvalidRequestException(CAN_NOT_SET_PENALTY, HttpStatus.BAD_REQUEST);
//            }
//            Long otpExpiration = foundedOtp.getOtpExpiration();

            if (bidDetail.getConfirmationExpiredTime().isAfter(Instant.now())){
                throw new InvalidRequestException("You can't set penalty before the confirmation time expired", HttpStatus.BAD_REQUEST);
            }
//            else {
//                otpService.deleteOtp(foundedOtp.getOtpNumber());
//            }
            stringBuilder.append("You didn't confirm your winning bid within 24 hours");
            bidDetail.setStatus(BidStatus.CANCELLED_BY_BIDDER);
            BidDetail save = bidDetailRepository.save(bidDetail);
            bidStatus = save.getStatus().getValue();
        } else if (bidDetail.getStatus().equals(BidStatus.CONFIRMED)){
            PaymentResponse payment = paymentService.getPaymentByUserIdAndItemId(bidDetail.getBidder().getId(), bidDetail.getItemId());
            if (payment.getPaymentStatus().equals(PaymentStatus.PENDING) && payment.getPaymentExpiration().isBefore(ZonedDateTime.now())){
                stringBuilder.append("You didn't complete the payment within 24 hours");
                PaymentResponse paymentResponse = paymentService.updatePaymentStatus(payment.getPaymentId(), PaymentStatus.CANCELLED);
                paymentStatus = paymentResponse.getPaymentStatus().getValue();
            }
        }else if (bidDetail.getStatus().equals(BidStatus.CANCELLED_BY_BIDDER) || paymentStatus.equals(PaymentStatus.CANCELLED.getValue())){
            stringBuilder.append("You cancelled your winning bid");
            bidStatus = bidDetail.getStatus().getValue();
        } else {
            throw new InvalidRequestException(CAN_NOT_SET_PENALTY, HttpStatus.BAD_REQUEST);
        }

        if (bidStatus.equals(BidStatus.CANCELLED_BY_BIDDER.getValue()) || paymentStatus.equals(PaymentStatus.CANCELLED.getValue())){
            auctionParticipantService.setPenalty(bidDetail.getItemId(), bidDetail.getBidder().getId());
//            auctionParticipantService.bulkRefundDeposit(bidDetail.getItemId());
//            itemService.updateItemStatus(bidDetail.getItemId(), ItemStatus.AVAILABLE);
            String penalizedReason = stringBuilder.toString();
            MessageTemplate message = MessageTemplate.builder()
                    .name("penalized")
                    .data(Map.of(
                            "reason", penalizedReason,
                            "penalty_amount", participant.getDepositAmount()
                    ))
                    .build();
            notificationService.sendNotification(NotificationChannel.EMAIL, "Penalty", message, bidDetail.getBidder().getEmail());
            return GeneralResponse.<String>builder()
                    .message("Penalty set successfully")
                    .data(null)
                    .timestamp(ZonedDateTime.now(ZoneId.of("UTC"))).build();
        }else {
            throw new InvalidRequestException(CAN_NOT_SET_PENALTY, HttpStatus.BAD_REQUEST);
        }

    }

    @Override
    public void setPaymentDetail(Integer bidDetailId) {
        BidDetail foundedBid =getBidDetailById(bidDetailId);
        PaymentSetRequest setRequest = PaymentSetRequest.builder()
                .userId(foundedBid.getBidder().getId())
                .itemId(foundedBid.getItemId())
                .paymentAmount(foundedBid.getTotalBid())
                .build();
        paymentService.setPaymentDetail(setRequest);
    }

    public static <T> Predicate<T> distinctKey(Function<? super T, ?> keyExtractor){
        Set<Object> seen = new HashSet<>();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public BidDetail getBidDetailById(Integer id){
        return bidDetailRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(BID_DETAIL, ID, id)
        );
    }
}
