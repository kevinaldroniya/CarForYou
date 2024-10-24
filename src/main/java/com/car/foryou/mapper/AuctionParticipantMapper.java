package com.car.foryou.mapper;

import com.car.foryou.dto.auctionparticipant.AuctionParticipantResponse;
import com.car.foryou.model.AuctionParticipant;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class AuctionParticipantMapper {

    private AuctionParticipantMapper() {
    }

    public static AuctionParticipantResponse mapToAuctionParticipantResponse(AuctionParticipant participant){
        if (participant == null) {
            throw new IllegalArgumentException("AuctionParticipant cannot be null");
        }
        return AuctionParticipantResponse.builder()
                .registrationId(participant.getId())
                .itemId(participant.getItemId())
                .username(participant.getParticipant() != null ? participant.getParticipant().getUsername() : null)
                .depositAmount(participant.getDepositAmount())
                .paymentMethod(participant.getPaymentMethod())
                .registrationStatus(participant.getRegistrationStatus())
                .cancelReason(participant.getCancelReason())
                .cancelTime(participant.getCancelTime() != null ? ZonedDateTime.ofInstant(participant.getCancelTime(), ZoneId.of("UTC")) : null)
                .build();
    }
}
