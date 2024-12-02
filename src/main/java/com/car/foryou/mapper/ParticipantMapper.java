package com.car.foryou.mapper;

import com.car.foryou.dto.participant.ParticipantResponse;
import com.car.foryou.model.Participant;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class ParticipantMapper {

    private ParticipantMapper() {
    }

    public static ParticipantResponse mapToAuctionParticipantResponse(Participant participant){
        if (participant == null) {
            throw new IllegalArgumentException("Participant cannot be null");
        }
        Long highestBid = participant.getHighestBid() == null ? 0 : participant.getHighestBid();
        ZonedDateTime createdAt = ZonedDateTime.ofInstant(participant.getCreatedAt(), ZoneId.of("UTC"));
        ZonedDateTime paymentExpired =  participant.getPaymentExpiry() == null ? null : ZonedDateTime.ofInstant(participant.getPaymentExpiry(), ZoneId.of("UTC"));
        ZonedDateTime confirmationExpired = participant.getConfirmationExpiry() == null ? null : ZonedDateTime.ofInstant(participant.getConfirmationExpiry(), ZoneId.of("UTC"));
        return ParticipantResponse.builder()
                .participantId(participant.getId())
                .createdAt(createdAt)
                .auctionId(participant.getAuction().getId())
                .depositStatus(participant.getDepositStatus())
                .highestBid(highestBid)
                .processStatus(participant.getAuctionProcessStatus())
                .paymentExpired(paymentExpired)
                .confirmationExpired(confirmationExpired)
                .userId(participant.getUser().getId())
                .build();
    }
}
