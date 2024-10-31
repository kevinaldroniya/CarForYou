package com.car.foryou.mapper;

import com.car.foryou.dto.bid.BidDetailResponse;
import com.car.foryou.model.BidDetail;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class BidDetailMapper {

    private BidDetailMapper(){}

    public static BidDetailResponse toBidDetailResponse(BidDetail bidDetail) {
        return BidDetailResponse.builder()
                .bidId(bidDetail.getId())
                .itemId(bidDetail.getItemId())
                .bidder(bidDetail.getBidder().getUsername())
                .bidAmount(bidDetail.getTotalBid())
                .bidStatus(bidDetail.getStatus())
                .bidTime(bidDetail.getBidTime().atZone(ZoneId.of("UTC")))
                .confirmationExpiredTime(ZonedDateTime.ofInstant(bidDetail.getConfirmationExpiredTime(), ZoneId.of("UTC")))
                .build();
    }
}
