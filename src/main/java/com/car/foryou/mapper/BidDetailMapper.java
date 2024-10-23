package com.car.foryou.mapper;

import com.car.foryou.dto.bid.BidDetailResponse;
import com.car.foryou.model.BidDetail;

import java.time.ZoneId;

public class BidDetailMapper {
    public static BidDetailResponse toBidDetailResponse(BidDetail bidDetail) {
        return BidDetailResponse.builder()
                .bidId(bidDetail.getId())
                .itemId(bidDetail.getItemId())
                .bidderName(bidDetail.getBidder().getFirstName() + " " + bidDetail.getBidder().getLastName())
                .bidAmount(bidDetail.getTotalBid())
                .bidTime(bidDetail.getBidTime().atZone(ZoneId.of("UTC")))
                .build();
    }
}
