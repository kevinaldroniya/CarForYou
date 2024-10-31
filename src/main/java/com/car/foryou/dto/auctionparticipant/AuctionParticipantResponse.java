package com.car.foryou.dto.auctionparticipant;

import com.car.foryou.dto.payment.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuctionParticipantResponse {
    private Integer registrationId;
    private Integer itemId;
    private String username;
    private Integer depositAmount;
    private ZonedDateTime registrationDate;
    private PaymentMethod paymentMethod;
    private AuctionRegistrationStatus registrationStatus;
    private String cancelReason;
    private ZonedDateTime cancelTime;
}
