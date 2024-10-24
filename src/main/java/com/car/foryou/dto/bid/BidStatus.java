package com.car.foryou.dto.bid;

import com.car.foryou.exception.InvalidRequestException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BidStatus {
    WAITING_FOR_CONFIRMATION("waiting_for_confirmation"),
    CANCELLED_BY_BIDDER("cancelled_by_bidder"),
    CONFIRMED("CONFIRMED"),
    PLACED("placed");

    private final String value;

    BidStatus(String value) {
        this.value = value;
    }

    public static BidStatus fromValue(String value) {
        for (BidStatus status : BidStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new InvalidRequestException("Invalid BidStatus : " + value, HttpStatus.BAD_REQUEST);
    }
}
