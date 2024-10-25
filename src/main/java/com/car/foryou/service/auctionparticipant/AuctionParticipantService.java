package com.car.foryou.service.auctionparticipant;

import com.car.foryou.dto.auctionparticipant.AuctionParticipantRequest;
import com.car.foryou.dto.auctionparticipant.AuctionParticipantResponse;
import com.car.foryou.dto.auctionparticipant.AuthParticipantCancelRequest;

import java.util.List;

public interface AuctionParticipantService {
    List<AuctionParticipantResponse> getAllAuctionParticipants();
    String register(Integer itemId, AuctionParticipantRequest request);
    String cancelRegistration(Integer itemId, AuthParticipantCancelRequest request);
    String refundDeposit(Integer registrationId);
    AuctionParticipantResponse getAuctionParticipantByItemIdAndUserId(Integer itemId, Integer userId);
    void setPenalty(Integer itemId, Integer userId);
    String bulkRefundDeposit(Integer itemId);
    void setWinner(Integer userId, Integer itemId);
    AuctionParticipantResponse getParticipantById(Integer participantId);
}
