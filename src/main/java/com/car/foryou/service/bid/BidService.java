package com.car.foryou.service.bid;

import com.car.foryou.dto.GeneralResponse;
import com.car.foryou.dto.bid.BidDetailResponse;
import com.car.foryou.dto.bid.BidUpdateRequest;

import java.util.List;

public interface BidService {
    List<BidDetailResponse> getAllBidsByAuctionId(Integer itemId);
    BidDetailResponse getBidDetailResponseById(Integer bidId);
    BidDetailResponse updateBidDetail(BidUpdateRequest request);
    List<Long> getHighestBid(Integer itemId);
    List<BidDetailResponse> getAuctionWinner(Integer itemId);
    GeneralResponse<String> placeBid(Integer itemId);
    GeneralResponse<String> sendWinnerConfirmation(Integer bidDetailId);
    GeneralResponse<String> confirmBidWinner(String signature);
//    GeneralResponse<String> bidWinnerConfirmation(BidConfirmationRequest request);
//    GeneralResponse<String> setPenalty(Integer bidDetailId);
    void setPaymentDetail(Integer bidDetailId);
}
