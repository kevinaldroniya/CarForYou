package com.car.foryou.dto.auctionparticipant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuctionParticipantUpdateRequest {
    private Integer id;
    private AuctionRegistrationStatus status;
}
