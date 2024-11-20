package com.car.foryou.dto.payment;

import com.car.foryou.dto.participant.ParticipantResponse;
import com.car.foryou.dto.bid.BidDetailResponse;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class PaymentConfirmationRequest {
    private BidDetailResponse bidDetail;
    private ParticipantResponse auctionParticipantResponse;
    private Integer otp;
    private ZonedDateTime expirationTime;
}
