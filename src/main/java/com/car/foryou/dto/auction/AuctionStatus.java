package com.car.foryou.dto.auction;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuctionStatus {
    ACTIVE("active"),
    CANCELLED("canceled"),
    ENDED("ended"),
    PAYMENT_CANCELED("payment_canceled"),
    PAYMENT_SUCCESS("payment_success");
    private final String value;
}
