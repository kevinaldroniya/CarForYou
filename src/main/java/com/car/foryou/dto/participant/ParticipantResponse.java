package com.car.foryou.dto.participant;

import com.car.foryou.dto.auction.AuctionProcessStatus;
import com.car.foryou.dto.payment.PaymentMethod;
import com.car.foryou.model.Participant;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParticipantResponse {
    private Integer participantId;
    private ZonedDateTime createdAt;
    private Integer userId;
    private Integer auctionId;
    private Participant.DepositStatus depositStatus;
    private Long highestBid;
    private AuctionProcessStatus processStatus;
    private ZonedDateTime confirmationExpired;
    private ZonedDateTime paymentExpired;
}
