package com.car.foryou.dto.auctionparticipant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuctionParticipantRefunded {
    private String registrationId;

    public String getRegistrationId() {
        return registrationId;
    }
}
