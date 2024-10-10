package com.car.foryou.dto;

import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public class AuctionParticipantResponse {
    private String registrationId;
    private Integer itemId;
    private String username;
    private Integer depositAmount;
    private ZonedDateTime registrationDate;
    private PaymentMethod paymentMethod;
    private AuctionRegistrationStatus registrationStatus;
    private String cancelReason;
    private ZonedDateTime cancelTime;
}
