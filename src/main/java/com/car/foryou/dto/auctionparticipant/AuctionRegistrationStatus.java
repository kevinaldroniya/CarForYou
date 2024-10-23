package com.car.foryou.dto.auctionparticipant;

import lombok.Getter;

@Getter
public enum AuctionRegistrationStatus {
    REGISTERED("REGISTERED"),
    CANCELLED("CANCELLED"),
    REFUNDED("REFUNDED"),
    PENALTY("PENALTY");

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
