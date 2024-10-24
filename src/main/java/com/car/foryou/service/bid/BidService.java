package com.car.foryou.service.bid;

import com.car.foryou.dto.bid.BidDetailResponse;
import com.car.foryou.dto.bid.BidStatus;
import com.car.foryou.model.BidDetail;

import java.util.List;

public interface BidService {
    String placeBid(Integer itemId);
    List<BidDetailResponse> getAllBidByItem(Integer itemId);
    BidDetailResponse getBidDetailById(Integer bidId);
    List<Long> getHighestBid(Integer itemId);
    List<BidDetailResponse> getAuctionWinner(Integer itemId);
    String sendWinnerConfirmation(Integer bidDetailId);
    String confirmBidWinner(String encodedBidId, String encodeEmail, String encodeOtp);
    BidDetailResponse setPenalty(Integer bidDetailId);
    BidDetail setBidStatus(Integer bidDetailId, BidStatus bidStatus);
}
