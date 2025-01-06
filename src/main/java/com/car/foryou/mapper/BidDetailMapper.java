package com.car.foryou.mapper;

import com.car.foryou.dto.bid.BidDetailResponse;
import com.car.foryou.model.Bid;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class BidDetailMapper {

    private BidDetailMapper(){}

    public static BidDetailResponse toBidDetailResponse(Bid bid) {
//        Instant confirmationExpiredTime = bid.getConfirmationExpiredTime() == null ? null : bid.getConfirmationExpiredTime();
//        ZonedDateTime confirmationExpiredTimeZoned = confirmationExpiredTime == null ? null : ZonedDateTime.ofInstant(confirmationExpiredTime, ZoneId.of("UTC"));
        return BidDetailResponse.builder()
                .bidId(bid.getId())
//                .itemId(bid.getItemId())
                .bidder(bid.getParticipant().getUser().getUsername())
                .bidAmount(bid.getBidAmount())
//                .bidStatus(bid.getStatus())
                .bidTime(bid.getCreatedAt().atZone(ZoneId.of("UTC")))
//                .confirmationExpiredTime(confirmationExpiredTimeZoned)
                .build();
    }
}
