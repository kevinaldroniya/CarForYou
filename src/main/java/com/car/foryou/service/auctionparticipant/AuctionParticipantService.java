package com.car.foryou.service.auctionparticipant;

import com.car.foryou.dto.GeneralResponse;
import com.car.foryou.dto.auctionparticipant.AuctionParticipantRequest;
import com.car.foryou.dto.auctionparticipant.AuctionParticipantResponse;
import com.car.foryou.dto.auctionparticipant.AuctionParticipantUpdateRequest;
import com.car.foryou.dto.auctionparticipant.AuthParticipantCancelRequest;

import java.util.List;

public interface AuctionParticipantService {
    List<AuctionParticipantResponse> getAllAuctionParticipants();
    AuctionParticipantResponse getParticipantResponseById(Integer participantId);
    AuctionParticipantResponse getParticipantResponseByItemIdAndUserId(Integer itemId, Integer userId);
    List<AuctionParticipantResponse> getParticipantResponseByItemId(Integer itemId);
    AuctionParticipantResponse updateParticipant(AuctionParticipantUpdateRequest request);
    GeneralResponse<AuctionParticipantResponse> register(Integer itemId, AuctionParticipantRequest request);
    GeneralResponse<AuctionParticipantResponse> cancelRegistration(Integer itemId, AuthParticipantCancelRequest request);
    GeneralResponse<AuctionParticipantResponse> refundDeposit(Integer registrationId);
    GeneralResponse<AuctionParticipantResponse> bulkRefundDeposit(Integer itemId);
    void setPenalty(Integer itemId, Integer userId);
    void setWinner(Integer userId, Integer itemId);
}
