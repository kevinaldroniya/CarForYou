package com.car.foryou.dto.payment;

import com.car.foryou.dto.auctionparticipant.AuctionParticipantResponse;
import com.car.foryou.dto.bid.BidDetailResponse;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class PaymentConfirmationRequest {
    private BidDetailResponse bidDetail;
    private AuctionParticipantResponse auctionParticipantResponse;
    private Integer otp;
    private ZonedDateTime expirationTime;
}
