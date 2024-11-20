package com.car.foryou.service.bid;

import com.car.foryou.dto.auction.AuctionStatus;
import com.car.foryou.dto.GeneralResponse;
import com.car.foryou.dto.bid.BidDetailResponse;
import com.car.foryou.dto.bid.BidStatus;
import com.car.foryou.dto.bid.BidUpdateRequest;
import com.car.foryou.dto.payment.PaymentConfirmationRequest;
import com.car.foryou.exception.*;
import com.car.foryou.helper.EncryptionHelper;
import com.car.foryou.mapper.BidDetailMapper;
import com.car.foryou.model.Bid;
import com.car.foryou.model.Participant;
import com.car.foryou.model.User;
import com.car.foryou.repository.bid.BidRepository;
import com.car.foryou.service.auction.AuctionService;
import com.car.foryou.service.participant.ParticipantService;
import com.car.foryou.service.notification.NotificationService;
import com.car.foryou.service.otp.OtpService;
import com.car.foryou.service.user.CustomUserDetailService;
import com.car.foryou.service.user.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;


@Service
@Slf4j
public class BidServiceImpl implements BidService{

    private final BidRepository bidRepository;
    private final ParticipantService participantService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final OtpService otpService;
    private final ObjectMapper objectMapper;
    private final EncryptionHelper encryptionHelper;
    private final AuctionService auctionService;

    private static final String NON_AUCTION_ITEM = "Auction not started yet or auction has ended";
    private static final String SUCCESSFUL_BID = "Bid placed successfully";
    private static final String BID_DETAIL = "Bid";
    private static final String ID = "ID";
    private static final long BID_INCREMENT = 500_000;
    private static final String CAN_NOT_SET_PENALTY = "You can't set penalty to this user";
    private static final String CAN_NOT_SENT_WINNER_CONFIRMATION = "You can't sent winner confirmation to this user";

    public BidServiceImpl(BidRepository bidRepository, ParticipantService participantService, UserService userService, NotificationService notificationService, OtpService otpService, ObjectMapper objectMapper, EncryptionHelper encryptionHelper, AuctionService auctionService) {
        this.bidRepository = bidRepository;
        this.participantService = participantService;
        this.userService = userService;
        this.notificationService = notificationService;
        this.otpService = otpService;
        this.objectMapper = objectMapper;
        this.encryptionHelper = encryptionHelper;
        this.auctionService = auctionService;
    }

    @Override
    @Transactional
    public GeneralResponse<String> placeBid(Integer auctionId) {
//        Auction auction = auctionService.getAuctionById(auctionId);
        Integer userId = CustomUserDetailService.getLoggedInUserDetails().getId();
        Participant participant = participantService.getParticipantByAuctionIdAndUserId(auctionId, userId);
        if (!participant.getAuction().getStatus().equals(AuctionStatus.ACTIVE)){
            throw new InvalidRequestException("Auction is not active", HttpStatus.BAD_REQUEST);
        }

        if (participant.getAuction().getStartDate().isAfter(Instant.now())){
            throw new InvalidRequestException("Auction not started yet", HttpStatus.BAD_REQUEST);
        } else if (participant.getAuction().getEndDate().isBefore(Instant.now())){
            throw new InvalidRequestException("Auction has ended", HttpStatus.BAD_REQUEST);
        }
        Long currentBid;
        Optional<Bid> currentTopBid = bidRepository.findByAuctionId(participant.getAuction().getId()).stream().findFirst();
        if (currentTopBid.isPresent()){
            currentBid = currentTopBid.get().getBidAmount();
        }else {
            currentBid = participant.getAuction().getItem().getStartingPrice();
        }
        Long finalBid = currentBid + BID_INCREMENT;
        Bid bid = Bid.builder()
                .user(participant.getUser())
                .auction(participant.getAuction())
                .bidAmount(finalBid)
                .status(BidStatus.PLACED)
                .build();
        bidRepository.save(bid);
        participantService.updateHighestBid(participant.getId(), finalBid);
        log.info("User {} placed bid of {} on auction {}", userId, finalBid, auctionId);
        return GeneralResponse.<String>builder()
                .message(SUCCESSFUL_BID)
                .data(null)
                .timestamp(ZonedDateTime.now(ZoneId.of("UTC"))).build();
    }

    @Override
    public List<BidDetailResponse> getAllBidsByAuctionId(Integer auctionId) {
        List<Bid> bids = bidRepository.findByAuctionId(auctionId);
//        ItemResponse itemById = itemService.getItemResponseById(itemId);
//        List<Bid> bids = bidRepository.findAllByItemId(itemById.getItemId());
//        return bids.stream().sorted((bid1, bid2) -> Long.compare(bid2.getBidAmount(), bid1.getBidAmount())).map(BidDetailMapper::toBidDetailResponse).toList();
        return bids.stream().map(BidDetailMapper::toBidDetailResponse).toList();
    }

    @Override
    public BidDetailResponse getBidDetailResponseById(Integer bidId) {
        Integer userReqId = CustomUserDetailService.getLoggedInUserDetails().getId();
        Bid bid = getBidDetailById(bidId);
        if (!bid.getUser().getId().equals(userReqId)){
            throw new InvalidRequestException("You are not the owner of this bid", HttpStatus.BAD_REQUEST);
        }
        return BidDetailMapper.toBidDetailResponse(bid);
    }

    @Override
    public BidDetailResponse updateBidDetail(BidUpdateRequest request) {
        Bid foundedBid = getBidDetailById(request.getBidId());
        foundedBid.setStatus(request.getBidStatus());
        return BidDetailMapper.toBidDetailResponse(bidRepository.save(foundedBid));
    }

    @Override
    public List<Long> getHighestBid(Integer itemId) {
//        List<Bid> bids = bidRepository.findAllByItemId(itemId);
//        // get top 3 highest bid
//        return bids.stream().sorted((bid1, bid2) -> Long.compare(bid2.getBidAmount(), bid1.getBidAmount())).limit(3).map(Bid::getBidAmount).toList();
        return null;
    }

    @Override
    public List<BidDetailResponse> getAuctionWinner(Integer itemId) {
//        ItemResponse item = itemService.getItemResponseById(itemId);
//        int foundedItemId = item.getItemId();
//        List<Bid> bids = bidRepository.findAllByItemId(foundedItemId);
//        return bids.stream()
//                // get the highest bid
//                .sorted((bid1, bid2) -> Long.compare(bid2.getBidAmount(), bid1.getBidAmount()))
//                // get the distinct bidder
//                .filter(distinctKey(Bid::getUser))
//                // map to BidDetailResponse
//                .map(BidDetailMapper::toBidDetailResponse)
//                .toList();
        return null;
    }

    @Override
    public GeneralResponse<String> sendWinnerConfirmation(Integer bidDetailId) {
//        Bid bid = bidRepository.findById(bidDetailId).orElseThrow(
//                () -> new ResourceNotFoundException(BID_DETAIL, ID, bidDetailId)
//        );
//        if (!bid.getStatus().equals(BidStatus.PLACED)){
//            throw new InvalidRequestException(CAN_NOT_SENT_WINNER_CONFIRMATION, HttpStatus.BAD_REQUEST);
//        }
//        List<BidDetailResponse> auctionWinner = this.getAuctionWinner(bid.getItemId());
//        int winnerIndex = 0;
//        for(BidDetailResponse winner : auctionWinner){
//
//            if (winner.getBidId() == bid.getId()){
//                winnerIndex = auctionWinner.indexOf(winner);
//                break;
//            }
//            winnerIndex++;
//        }
//
//        if (winnerIndex > 0 && auctionWinner.get(winnerIndex -1).getBidStatus().equals(BidStatus.PLACED)){
//            throw new InvalidRequestException(CAN_NOT_SENT_WINNER_CONFIRMATION, HttpStatus.BAD_REQUEST);
//        }else if (auctionWinner.get(0).getBidId() != bid.getId()){
//            throw new InvalidRequestException(CAN_NOT_SENT_WINNER_CONFIRMATION, HttpStatus.BAD_REQUEST);
//        }
////        ParticipantResponse participantResponse = participantService.getParticipantResponseByItemIdAndUserId(bid.getItemId(), bid.getBidder().getId());
//        ItemResponse item = itemService.getItemResponseById(bid.getItemId());
////        OtpResponse otp = otpService.generateOtp(bid.getBidder().getEmail(), OtpType.WINNER_CONFIRMATION);
////        BidDetailResponse bidDetailResponse = BidDetailMapper.toBidDetailResponse(bid);
////        PaymentConfirmationRequest request = PaymentConfirmationRequest.builder()
////                .bid(bidDetailResponse)
////                .auctionParticipantResponse(participantResponse)
////                .otp(517077)
////                .expirationTime(ZonedDateTime.ofInstant(Instant.ofEpochSecond(1730358263), ZoneId.of("UTC")))
////                .build();
////        try {
////            String stringRequest = objectMapper.writeValueAsString(request);
////            String encrypted = encryptionHelper.encrypt(stringRequest);
////            String winnerConfirmationLink = "http://localhost:8080/bid/confirm?signature=" + encrypted;
//            MessageTemplate message = MessageTemplate.builder()
//                    .name("auctionWinnerConfirmation")
//                    .data(Map.of(
//                            "item_name", item.getTitle(),
//                            "winning_bid", bid.getBidAmount()
////                            , "confirmation_link", winnerConfirmationLink
//                    ))
//                    .build();
//            bid.setConfirmationExpiredTime(Instant.now().plus(24, ChronoUnit.HOURS));
//            bid.setStatus(BidStatus.WAITING_FOR_CONFIRMATION);
//            bidRepository.save(bid);
//            notificationService.sendNotification(NotificationChannel.EMAIL, "Auction Winner Confirmation", message, bid.getUser().getEmail());
//            return GeneralResponse.<String>builder()
//                    .message("Winner confirmation sent successfully")
//                    .data(null)
//                    .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
//                    .build();
////        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | IOException | InvalidKeyException e){
////            throw new GeneralException("There is an issue with our system, please try again, if the issue persists, please contact our support", HttpStatus.INTERNAL_SERVER_ERROR);
////        }
        return null;

    }

    @Override
    public GeneralResponse<String> confirmBidWinner(String signature) {
        try {
            String decrypted = encryptionHelper.decrypt(signature);
            PaymentConfirmationRequest confirmationRequest = getConfirmationRequest(decrypted);
            Bid bid = bidRepository.findById(confirmationRequest.getBidDetail().getBidId()).orElseThrow(
                    () -> new ResourceNotFoundException(BID_DETAIL, ID, confirmationRequest.getBidDetail().getBidId())
            );
            if (!bid.getStatus().equals(BidStatus.WAITING_FOR_CONFIRMATION)){
                throw new InvalidRequestException("You are not the winner of this auction", HttpStatus.BAD_REQUEST);
            }

//            User user = userService.getUserByEmailOrUsernameOrPhoneNumber(confirmationRequest.getAuctionParticipantResponse().getUsername());

//            if (!bid.getUser().getEmail().equals(user.getEmail())){
//                throw new InvalidRequestException("You are not the winner of this auction", HttpStatus.BAD_REQUEST);
//            }
            otpService.unSignOtpVerify(confirmationRequest.getOtp(), bid.getUser().getEmail());
            bid.setStatus(BidStatus.CONFIRMED);
            bidRepository.save(bid);
            setPaymentDetail(bid.getId());
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

//    @Override
//    public GeneralResponse<String> bidWinnerConfirmation(BidConfirmationRequest request) {
//        Set<BidStatus> allowedBidStatus = Set.of(BidStatus.CONFIRMED, BidStatus.CANCELLED_BY_BIDDER);
//        if (!allowedBidStatus.contains(request.bidStatus())){
//            throw new InvalidRequestException("Invalid BidStatus : " + request.bidStatus(), HttpStatus.BAD_REQUEST);
//        }
//        GeneralResponse<String> response = GeneralResponse.<String>builder()
//                .data(null)
//                .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
//                .build();
//        Bid bid = getBidDetailById(request.bidDetailId());
//        if (!bid.getStatus().equals(BidStatus.WAITING_FOR_CONFIRMATION) ||
//                !bid.getBidder().getId().equals(CustomUserDetailService.getLoggedInUserDetails().getId())){
//            throw new InvalidRequestException("You are not the winner of this auction", HttpStatus.BAD_REQUEST);
//        }
//        if (bid.getConfirmationExpiredTime().isBefore(Instant.now())){
//            throw new ResourceExpiredException("BidConfirmation");
//        }
//        bid.setStatus(request.bidStatus());
//        bidRepository.save(bid);
//
//        List<BidDetailResponse> auctionWinner = getAuctionWinner(bid.getItemId());
//
//        if (request.bidStatus().equals(BidStatus.CONFIRMED)){
//            setPaymentDetail(bid.getId());
//            response.setMessage("You have confirmed your winning bid, please proceed to payment");
//        }else if (request.bidStatus().equals(BidStatus.CANCELLED_BY_BIDDER) && auctionWinner.get(0).getBidId() == bid.getId()){
//            participantService.setPenalty(bid.getItemId(), bid.getBidder().getId());
//            setPenalty(bid.getId());
//            response.setMessage("You have cancelled your winning bid, your deposit will be forfeited");
//        }else{
//            response.setMessage("You have cancelled your winning bid");
//        }
//        return response;
//    }

    private PaymentConfirmationRequest getConfirmationRequest(String decodeRequest) {
        try {
            return objectMapper.readValue(decodeRequest, new TypeReference<PaymentConfirmationRequest>() {});
        } catch (JsonProcessingException e) {
            throw new ConversionException("request", "PaymentConfirmationRequest", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @Transactional
//    @Override
//    public GeneralResponse<String> setPenalty(Integer bidDetailId) {
//        Bid bid = bidRepository.findById(bidDetailId).orElseThrow(
//                () -> new ResourceNotFoundException(BID_DETAIL, ID, bidDetailId)
//        );
//
//        List<BidDetailResponse> auctionWinner = getAuctionWinner(bid.getItemId());
//        if (auctionWinner.get(0).getBidId() != bid.getId()){
//            throw new InvalidRequestException(CAN_NOT_SET_PENALTY, HttpStatus.BAD_REQUEST);
//        }
//        ParticipantResponse participant = participantService.getParticipantResponseByItemIdAndUserId(bid.getItemId(), bid.getBidder().getId());
////        Otp foundedOtp = null;
//        String bidStatus = "";
//        String paymentStatus = "";
//        StringBuilder stringBuilder = new StringBuilder();
//        if (bid.getStatus().equals(BidStatus.WAITING_FOR_CONFIRMATION)){
////            try {
////                foundedOtp = otpService.getOtpByUserAndOtpType(bid.getBidder().getEmail(), OtpType.WINNER_CONFIRMATION);
////            }catch (ResourceNotFoundException e) {
////                throw new InvalidRequestException(CAN_NOT_SET_PENALTY, HttpStatus.BAD_REQUEST);
////            }
////            Long otpExpiration = foundedOtp.getOtpExpiration();
//
//            if (bid.getConfirmationExpiredTime().isAfter(Instant.now())){
//                throw new InvalidRequestException("You can't set penalty before the confirmation time expired", HttpStatus.BAD_REQUEST);
//            }
////            else {
////                otpService.deleteOtp(foundedOtp.getOtpNumber());
////            }
//            stringBuilder.append("You didn't confirm your winning bid within 24 hours");
//            bid.setStatus(BidStatus.CANCELLED_BY_BIDDER);
//            Bid save = bidRepository.save(bid);
//            bidStatus = save.getStatus().getValue();
//        } else if (bid.getStatus().equals(BidStatus.CONFIRMED)){
//            PaymentResponse payment = paymentService.getPaymentByUserIdAndItemId(bid.getBidder().getId(), bid.getItemId());
//            if (payment.getPaymentStatus().equals(PaymentStatus.PENDING) && payment.getPaymentExpiration().isBefore(ZonedDateTime.now())){
//                stringBuilder.append("You didn't complete the payment within 24 hours");
//                PaymentResponse paymentResponse = paymentService.updatePaymentStatus(payment.getPaymentId(), PaymentStatus.CANCELLED);
//                paymentStatus = paymentResponse.getPaymentStatus().getValue();
//            }
//        }else if (bid.getStatus().equals(BidStatus.CANCELLED_BY_BIDDER) || paymentStatus.equals(PaymentStatus.CANCELLED.getValue())){
//            stringBuilder.append("You cancelled your winning bid");
//            bidStatus = bid.getStatus().getValue();
//        } else {
//            throw new InvalidRequestException(CAN_NOT_SET_PENALTY, HttpStatus.BAD_REQUEST);
//        }
//
//        if (bidStatus.equals(BidStatus.CANCELLED_BY_BIDDER.getValue()) || paymentStatus.equals(PaymentStatus.CANCELLED.getValue())){
//            participantService.setPenalty(bid.getItemId(), bid.getBidder().getId());
////            participantService.bulkRefundDeposit(bid.getItemId());
////            itemService.updateItemStatus(bid.getItemId(), ItemStatus.AVAILABLE);
//            String penalizedReason = stringBuilder.toString();
//            MessageTemplate message = MessageTemplate.builder()
//                    .name("penalized")
//                    .data(Map.of(
//                            "reason", penalizedReason,
//                            "penalty_amount", participant.getDepositAmount()
//                    ))
//                    .build();
//            notificationService.sendNotification(NotificationChannel.EMAIL, "Penalty", message, bid.getBidder().getEmail());
//            return GeneralResponse.<String>builder()
//                    .message("Penalty set successfully")
//                    .data(null)
//                    .timestamp(ZonedDateTime.now(ZoneId.of("UTC"))).build();
//        }else {
//            throw new InvalidRequestException(CAN_NOT_SET_PENALTY, HttpStatus.BAD_REQUEST);
//        }
//
//    }

    @Override
    public void setPaymentDetail(Integer bidDetailId) {
//        Bid foundedBid =getBidDetailById(bidDetailId);
//        PaymentSetRequest setRequest = PaymentSetRequest.builder()
//                .userId(foundedBid.getUser().getId())
//                .itemId(foundedBid.getItemId())
//                .paymentAmount(foundedBid.getBidAmount())
//                .build();
//        paymentService.setPaymentDetail(setRequest);

    }

    public static <T> Predicate<T> distinctKey(Function<? super T, ?> keyExtractor){
        Set<Object> seen = new HashSet<>();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public Bid getBidDetailById(Integer id){
        return bidRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(BID_DETAIL, ID, id)
        );
    }
}
