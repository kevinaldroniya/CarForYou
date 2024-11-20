package com.car.foryou.dto.auction;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuctionStatus {
    ACTIVE("active"),
    CANCELLED("canceled"),
    ENDED("ended");

    private final String value;
}