package com.car.foryou.service.bid;

import com.car.foryou.dto.GeneralResponse;
import com.car.foryou.dto.bid.BidDetailResponse;
import com.car.foryou.dto.bid.BidUpdateRequest;

import java.util.List;

public interface BidService {
    List<BidDetailResponse> getAllBidsByAuctionId(Integer itemId);

    BidDetailResponse getBidDetailResponseById(Integer bidId);

    GeneralResponse<String> placeBid(Integer itemId);
}
