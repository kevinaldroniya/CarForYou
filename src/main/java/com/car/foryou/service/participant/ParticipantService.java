package com.car.foryou.service.participant;

import com.car.foryou.dto.auction.AuctionProcessStatus;
import com.car.foryou.dto.participant.ParticipantResponse;
import com.car.foryou.model.Participant;

import java.util.List;

public interface ParticipantService {
    List<Participant> getAllParticipantsV2();
    ParticipantResponse getParticipantResponseV2(Integer participantId);
    Participant getParticipantByIdV2(Integer participantId);
    ParticipantResponse createParticipant(Integer auctionId);
    Participant updateParticipantV2(Integer participantId);
    Participant deleteParticipant(Integer participantId);
    List<Participant> getParticipantByAuctionId(Integer auctionId);
    Participant updateDepositStatus(Integer id, Participant.DepositStatus depositStatus);
    Participant updateAuctionProcessStatus(Integer participantId, AuctionProcessStatus status);
    Participant getParticipantByAuctionIdAndUserId(Integer auctionId, Integer userId);
//    void updateHighestBid(Integer id, Long finalBid);
    List<ParticipantResponse> getParticipantResponseByAuctionId(Integer auctionId);
    ParticipantResponse sendConfirmationToParticipant(Integer auctionId);
    ParticipantResponse confirmTheAuction(Integer participantId);
    ParticipantResponse cancelAuctionProcess(Integer participantId);
    ParticipantResponse setPenalty(Integer participantId);
}
