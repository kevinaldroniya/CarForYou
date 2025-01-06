package com.car.foryou.dto.bid;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BidRequest {
    private Integer auctionId;
    private Integer userId;
    private Runnable bidHandler;
}
