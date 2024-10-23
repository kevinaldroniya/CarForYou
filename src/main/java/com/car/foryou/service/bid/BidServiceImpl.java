package com.car.foryou.service.bid;

import com.car.foryou.dto.auctionparticipant.AuctionParticipantResponse;
import com.car.foryou.dto.auctionparticipant.AuctionRegistrationStatus;
import com.car.foryou.dto.bid.BidDetailResponse;
import com.car.foryou.dto.bid.BidStatus;
import com.car.foryou.dto.item.ItemResponse;
import com.car.foryou.dto.item.ItemStatus;
import com.car.foryou.dto.user.UserResponse;
import com.car.foryou.exception.InvalidRequestException;
import com.car.foryou.exception.ResourceNotFoundException;
import com.car.foryou.mapper.BidDetailMapper;
import com.car.foryou.model.BidDetail;
import com.car.foryou.model.User;
import com.car.foryou.repository.bid.BidDetailRepository;
import com.car.foryou.service.auctionparticipant.AuctionParticipantService;
import com.car.foryou.service.item.ItemService;
import com.car.foryou.service.user.CustomUserDetailService;
import com.car.foryou.service.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;


@Service
public class BidServiceImpl implements BidService{

    private final BidDetailRepository bidDetailRepository;
    private final ItemService itemService;
    private final AuctionParticipantService auctionParticipantService;
    private final UserService userService;

    private static final String NON_AUCTION_ITEM = "Item is not on auction, or auction has ended";
    private static final String SUCCESSFUL_BID = "Bid placed successfully";
    private static final String BID_DETAIL = "BidDetail";
    private static final String ID = "ID";
    private static final long BID_INCREMENT = 500_000;

    public BidServiceImpl(BidDetailRepository bidDetailRepository, ItemService itemService, AuctionParticipantService auctionParticipantService, UserService userService) {
        this.bidDetailRepository = bidDetailRepository;
        this.itemService = itemService;
        this.auctionParticipantService = auctionParticipantService;
        this.userService = userService;
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

    public static <T> Predicate<T> distinctKey(Function<? super T, ?> keyExtractor){
        Set<Object> seen = new HashSet<>();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
