package com.car.foryou.service.bid;

import com.car.foryou.dto.auctionparticipant.AuctionParticipantResponse;
import com.car.foryou.dto.auctionparticipant.AuctionRegistrationStatus;
import com.car.foryou.dto.bid.BidDetailResponse;
import com.car.foryou.dto.bid.BidStatus;
import com.car.foryou.dto.item.ItemResponse;
import com.car.foryou.dto.item.ItemStatus;
import com.car.foryou.dto.notification.MessageTemplate;
import com.car.foryou.dto.notification.NotificationChannel;
import com.car.foryou.dto.otp.OtpResponse;
import com.car.foryou.dto.otp.OtpType;
import com.car.foryou.dto.payment.PaymentRequest;
import com.car.foryou.dto.user.UserResponse;
import com.car.foryou.exception.InvalidRequestException;
import com.car.foryou.exception.ResourceNotFoundException;
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
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
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

    private static final String NON_AUCTION_ITEM = "Item is not on auction, or auction has ended";
    private static final String SUCCESSFUL_BID = "Bid placed successfully";
    private static final String BID_DETAIL = "BidDetail";
    private static final String ID = "ID";
    private static final long BID_INCREMENT = 500_000;

    public BidServiceImpl(BidDetailRepository bidDetailRepository, ItemService itemService, AuctionParticipantService auctionParticipantService, UserService userService, NotificationService notificationService, OtpService otpService, PaymentService paymentService) {
        this.bidDetailRepository = bidDetailRepository;
        this.itemService = itemService;
        this.auctionParticipantService = auctionParticipantService;
        this.userService = userService;
        this.notificationService = notificationService;
        this.otpService = otpService;
        this.paymentService = paymentService;
    }

    @Override
    @Transactional
    public String placeBid(Integer itemId) {
        ItemResponse itemById = itemService.getItemById(itemId);
        if (!itemById.getStatus().equals(ItemStatus.ON_AUCTION) || itemById.getAuctionEnd().isBefore(ZonedDateTime.now())){
            throw new InvalidRequestException(NON_AUCTION_ITEM, HttpStatus.BAD_REQUEST);
        }

        AuctionParticipantResponse participant = auctionParticipantService.getAuctionParticipantByItemIdAndUserId(itemId, CustomUserDetailService.getLoggedInUserDetails().getId());

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
        UserResponse userFounded = userService.getUserByEmailOrUsernameOrPhoneNumber(participant.getUsername());
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
        return SUCCESSFUL_BID;
    }

    @Override
    public List<BidDetailResponse> getAllBidByItem(Integer itemId) {
        ItemResponse itemById = itemService.getItemById(itemId);
        List<BidDetail> bidDetails = bidDetailRepository.findAllByItemId(itemById.getItemId());
        return bidDetails.stream().sorted((bid1, bid2) -> Long.compare(bid2.getTotalBid(), bid1.getTotalBid())).map(BidDetailMapper::toBidDetailResponse).toList();
    }

    @Override
    public BidDetailResponse getBidDetailById(Integer bidId) {
        BidDetail bidDetail = bidDetailRepository.findById(bidId).orElseThrow(
                () -> new ResourceNotFoundException(BID_DETAIL, ID, HttpStatus.NOT_FOUND)
        );
        return BidDetailMapper.toBidDetailResponse(bidDetail);
    }

    @Override
    public List<Long> getHighestBid(Integer itemId) {
        List<BidDetail> bidDetails = bidDetailRepository.findAllByItemId(itemId);
        // get top 3 highest bid
        return bidDetails.stream().sorted((bid1, bid2) -> Long.compare(bid2.getTotalBid(), bid1.getTotalBid())).limit(3).map(BidDetail::getTotalBid).toList();
    }

    @Override
    public List<BidDetailResponse> getAuctionWinner(Integer itemId) {
        ItemResponse item = itemService.getItemById(itemId);
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
    public String sendWinnerConfirmation(Integer bidDetailId) {
        BidDetail bidDetail = bidDetailRepository.findById(bidDetailId).orElseThrow(
                () -> new ResourceNotFoundException(BID_DETAIL, ID, bidDetailId)
        );
        if (!bidDetail.getStatus().equals(BidStatus.PLACED)){
            throw new InvalidRequestException("Invalid", HttpStatus.BAD_REQUEST);
        }
        List<BidDetailResponse> auctionWinner = this.getAuctionWinner(bidDetail.getItemId());
        if (auctionWinner.get(0).getBidId() != bidDetail.getId()){
            throw new InvalidRequestException("Invalid", HttpStatus.BAD_REQUEST);
        }
        ItemResponse item = itemService.getItemById(bidDetail.getItemId());
        String encodeBidId = Base64.getEncoder().encodeToString(bidDetail.getId().toString().getBytes(StandardCharsets.UTF_8));
        String encodeEmail = Base64.getEncoder().encodeToString(bidDetail.getBidder().getEmail().getBytes(StandardCharsets.UTF_8));
        Integer otp = otpService.generateOtp(bidDetail.getBidder().getEmail()).getOtp();
        String encodeOtp = Base64.getEncoder().encodeToString(otp.toString().getBytes(StandardCharsets.UTF_8));
        String winnerConfirmationLink = "http://localhost:8080/bid/confirm/" + encodeBidId + "/" + encodeEmail + "/" + encodeOtp;
        MessageTemplate message = MessageTemplate.builder()
                .name("auctionWinnerConfirmation")
                .data(Map.of(
                        "item_name", item.getTitle(),
                        "winning_bid", bidDetail.getTotalBid(),
                        "confirmation_link", winnerConfirmationLink
                ))
                .build();
        bidDetail.setStatus(BidStatus.WAITING_FOR_CONFIRMATION);
        bidDetailRepository.save(bidDetail);
        notificationService.sendNotification(NotificationChannel.EMAIL, "Auction Winner Confirmation", message, bidDetail.getBidder().getEmail());
        return "Winner confirmation sent successfully";
    }

    @Override
    public String confirmBidWinner(String encodedBidId, String encodeEmail, String encodeOtp) {
        Integer bidId = Integer.valueOf(new String(Base64.getDecoder().decode(encodedBidId), StandardCharsets.UTF_8));
        String email = new String(Base64.getDecoder().decode(encodeEmail), StandardCharsets.UTF_8);
        Integer otp = Integer.valueOf(new String(Base64.getDecoder().decode(encodeOtp), StandardCharsets.UTF_8));
        BidDetail bidDetail = bidDetailRepository.findById(bidId).orElseThrow(
                () -> new ResourceNotFoundException(BID_DETAIL, ID, encodedBidId)
        );
        if (!bidDetail.getStatus().equals(BidStatus.WAITING_FOR_CONFIRMATION)){
            throw new InvalidRequestException("Invalid", HttpStatus.BAD_REQUEST);
        }

        if (!bidDetail.getBidder().getEmail().equals(email)){
            throw new InvalidRequestException("Invalid", HttpStatus.BAD_REQUEST);
        }
        otpService.otherOtpVerify(otp, bidDetail.getBidder().getEmail());

        return "You are confirmed as the winner of the auction, you can now proceed to payment, make sure to complete the payment within 24 hours or your deposit will be forfeited";
    }

    @Override
    public BidDetailResponse setPenalty(Integer bidDetailId) {
        BidDetail bidDetail = bidDetailRepository.findById(bidDetailId).orElseThrow(
                () -> new ResourceNotFoundException(BID_DETAIL, ID, bidDetailId)
        );
        AuctionParticipantResponse participant = auctionParticipantService.getAuctionParticipantByItemIdAndUserId(bidDetail.getItemId(), bidDetail.getBidder().getId());
        Otp foundedOtp = otpService.getOtpByUserAndOtpType(bidDetail.getBidder().getEmail(), OtpType.OTHER);
        Long otpExpiration = foundedOtp.getOtpExpiration();
        if (ZonedDateTime.now().toEpochSecond() < otpExpiration){
            throw new InvalidRequestException("You can't set penalty before the confirmation time expired", HttpStatus.BAD_REQUEST);
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (bidDetail.getStatus().equals(BidStatus.WAITING_FOR_CONFIRMATION)){
            stringBuilder.append("You didn't confirm your winning bid within 24 hours");
        }else if (bidDetail.getStatus().equals(BidStatus.WAITING_FOR_PAYMENT)) {
            stringBuilder.append("You didn't complete the payment within 24 hours");
        }
        String penalizedReason = stringBuilder.toString();
        bidDetail.setStatus(BidStatus.CANCELLED_BY_BIDDER);
        BidDetail save = bidDetailRepository.save(bidDetail);
        auctionParticipantService.setPenalty(bidDetail.getItemId(), bidDetail.getBidder().getId());
        MessageTemplate message = MessageTemplate.builder()
                .name("penalized")
                .data(Map.of(
                        "reason", penalizedReason,
                        "penalty_amount", participant.getDepositAmount()
                ))
                .build();
        notificationService.sendNotification(NotificationChannel.EMAIL, "Penalty", message, bidDetail.getBidder().getEmail());
        return BidDetailMapper.toBidDetailResponse(save);
    }

    @Override
    public BidDetail setBidStatus(Integer bidDetailId, BidStatus bidStatus) {
        BidDetail bidDetail = bidDetailRepository.findById(bidDetailId).orElseThrow(
                () -> new ResourceNotFoundException(BID_DETAIL, ID, bidDetailId)
        );
        if (bidStatus.equals(BidStatus.WIN))
        return null;
    }

    public static <T> Predicate<T> distinctKey(Function<? super T, ?> keyExtractor){
        Set<Object> seen = new HashSet<>();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
