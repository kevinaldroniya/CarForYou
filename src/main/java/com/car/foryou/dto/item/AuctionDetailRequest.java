package com.car.foryou.dto.item;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class AuctionDetailRequest {
    private ZonedDateTime auctionStart;
    private ZonedDateTime auctionEnd;
}
