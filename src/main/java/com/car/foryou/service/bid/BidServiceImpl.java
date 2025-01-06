package com.car.foryou.service.bid;

import com.car.foryou.dto.auction.AuctionStatus;
import com.car.foryou.dto.GeneralResponse;
import com.car.foryou.dto.bid.BidDetailResponse;
import com.car.foryou.dto.bid.BidRequest;
import com.car.foryou.dto.bid.BidStatus;
import com.car.foryou.dto.bid.BidUpdateRequest;
import com.car.foryou.dto.payment.PaymentConfirmationRequest;
import com.car.foryou.exception.*;
import com.car.foryou.helper.AuctionLockManager;
import com.car.foryou.helper.BidProcessingHelper;
import com.car.foryou.helper.EncryptionHelper;
import com.car.foryou.mapper.BidDetailMapper;
import com.car.foryou.model.Auction;
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
import org.springframework.security.concurrent.DelegatingSecurityContextRunnable;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.Predicate;


@Service
@Slf4j
public class BidServiceImpl implements BidService{

    private final BidRepository bidRepository;
    private final ParticipantService participantService;
    private final AuctionService auctionService;
    private final BidProcessingHelper bidProcessingHelper;

    private static final Lock lock = new ReentrantLock();
    private final Map<Auction, Lock> auctionLocks = new ConcurrentHashMap<>();
    private static final String SUCCESSFUL_BID = "Bid placed successfully";
    private static final String BID_DETAIL = "Bid";
    private static final String ID = "ID";
    private static final long BID_INCREMENT = 1_000_000;

    public BidServiceImpl(BidRepository bidRepository, ParticipantService participantService, AuctionService auctionService, BidProcessingHelper bidProcessingHelper) {
        this.bidRepository = bidRepository;
        this.participantService = participantService;
        this.auctionService = auctionService;
        this.bidProcessingHelper = bidProcessingHelper;
    }

    private final Map<Integer, AtomicLong> auctionTopBids = new ConcurrentHashMap<>();

    @Override
    public GeneralResponse<String> placeBid(Integer auctionId) {
        Integer userId = CustomUserDetailService.getLoggedInUserDetails().getId();

        Runnable bidHandler = () -> {
            Participant participant = participantService.getParticipantByAuctionIdAndUserId(auctionId, userId);
            validateAuctionTime(participant);

            Auction auction = participant.getAuction();
            Long currentBid = auction.getTopBid() == null || auction.getTopBid() == 0
                    ? auction.getItem().getStartingPrice()
                    : auction.getTopBid();

            Long finalBid = currentBid + BID_INCREMENT;
            log.info("User {} placing bid: currentBid={}, finalBid={}, auctionId={}", participant.getUser().getUsername(), currentBid, finalBid, auctionId);

            Bid bid = Bid.builder()
                    .participant(participant)
                    .auction(auction)
                    .bidAmount(finalBid)
                    .build();

            bidRepository.save(bid);

            auctionService.updateTopBid(auctionId, finalBid);
            log.info("User {} successfully placed bid of {} on auction {}", participant.getUser().getUsername(), finalBid, auctionId);
        };

        // Wrap the bidHandler with DelegatingSecurityContextRunnable
        Runnable securedBidHandler = new DelegatingSecurityContextRunnable(bidHandler, SecurityContextHolder.getContext());

        // Submit the bid request to the queue
        bidProcessingHelper.submitBid(new BidRequest(auctionId, userId, securedBidHandler));

        return GeneralResponse.<String>builder()
                .message("Bid submitted successfully. Processing...")
                .data(null)
                .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
                .build();
    }



    private void validateAuctionTime(Participant participant){
        if (!participant.getAuction().getStatus().equals(AuctionStatus.ACTIVE)){
            throw new InvalidRequestException("Auction is not active", HttpStatus.BAD_REQUEST);
        }
        if (participant.getAuction().getStartDate().isAfter(Instant.now())){
            throw new InvalidRequestException("Auction not started yet", HttpStatus.BAD_REQUEST);
        } else if (participant.getAuction().getEndDate().isBefore(Instant.now())){
            throw new InvalidRequestException("Auction has ended", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public List<BidDetailResponse> getAllBidsByAuctionId(Integer auctionId) {
        List<Bid> bids = bidRepository.findByAuctionId(auctionId);
        return bids.stream().map(BidDetailMapper::toBidDetailResponse).toList();
    }

    @Override
    public BidDetailResponse getBidDetailResponseById(Integer bidId) {
        Integer userReqId = CustomUserDetailService.getLoggedInUserDetails().getId();
        Bid bid = getBidDetailById(bidId);
        if (!bid.getCreatedBy().equals(userReqId)){
            throw new InvalidRequestException("You are not the owner of this bid", HttpStatus.BAD_REQUEST);
        }
        return BidDetailMapper.toBidDetailResponse(bid);
    }

    public Bid getBidDetailById(Integer id){
        return bidRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(BID_DETAIL, ID, id)
        );
    }
}
