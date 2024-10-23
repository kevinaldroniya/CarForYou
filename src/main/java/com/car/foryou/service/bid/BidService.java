package com.car.foryou.service.bid;

import com.car.foryou.dto.bid.BidDetailResponse;

import java.util.List;

public interface BidService {
    String placeBid(Integer itemId);
    List<BidDetailResponse> getAllBidByItem(Integer itemId);
    BidDetailResponse getBidDetailById(Integer bidId);
    List<Long> getHighestBid(Integer itemId);
    List<BidDetailResponse> getAuctionWinner(Integer itemId);

}
