package com.car.foryou.service.participant;

import com.car.foryou.dto.participant.ParticipantResponse;
import com.car.foryou.model.Participant;

import java.util.List;

public interface ParticipantService {
//    List<ParticipantResponse> getAllAuctionParticipants();
//    ParticipantResponse getParticipantResponseById(Integer participantId);
//    ParticipantResponse getParticipantResponseByItemIdAndUserId(Integer itemId, Integer userId);
//    List<ParticipantResponse> getParticipantResponseByItemId(Integer itemId);
//    ParticipantResponse updateParticipant(AuctionParticipantUpdateRequest request);
//    GeneralResponse<ParticipantResponse> register(Integer itemId, AuctionParticipantRequest request);
//    GeneralResponse<ParticipantResponse> cancelRegistration(Integer itemId, AuthParticipantCancelRequest request);
//    GeneralResponse<ParticipantResponse> refundDeposit(Integer registrationId);
//    GeneralResponse<ParticipantResponse> bulkRefundDeposit(Integer itemId);
//    void setPenalty(Integer itemId, Integer userId);
//    void setWinner(Integer userId, Integer itemId);
    List<Participant> getAllParticipantsV2();
    ParticipantResponse getParticipantResponseV2(Integer participantId);
    Participant getParticipantByIdV2(Integer participantId);
    ParticipantResponse createParticipant(Integer auctionId);
    Participant updateParticipantV2(Integer participantId);
    Participant deleteParticipant(Integer participantId);
    List<Participant> getParticipantByAuctionId(Integer auctionId);
    Participant updateDepositStatus(Integer id, Participant.DepositStatus depositStatus);
    Participant updateAuctionProcessStatus(Integer participantId, Participant.AuctionProcessStatus status);
    Participant getParticipantByAuctionIdAndUserId(Integer auctionId, Integer userId);
    void updateHighestBid(Integer id, Long finalBid);
}
