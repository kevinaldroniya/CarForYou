package com.car.foryou.dto.participant;

import lombok.Getter;

@Getter
public enum AuctionRegistrationStatus {
    REGISTERED("REGISTERED"),
    CANCELLED("CANCELLED"),
    REFUNDED("REFUNDED"),
    PENALTY("PENALTY"),
    WINNER("WINNER");

    private final String value;

    AuctionRegistrationStatus(String value) {
        this.value = value;
    }

    public static AuctionRegistrationStatus fromString(String text){
        for (AuctionRegistrationStatus status : AuctionRegistrationStatus.values()){
            if (text.equalsIgnoreCase(status.value)){
                return status;
            }
        }
        throw new IllegalArgumentException("No constant found with value : " + text);
    }
}
