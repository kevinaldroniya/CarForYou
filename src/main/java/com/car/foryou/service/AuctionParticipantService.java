package com.car.foryou.service;

import com.car.foryou.dto.AuctionParticipantRequest;
import com.car.foryou.dto.AuctionParticipantResponse;
import com.car.foryou.dto.CancelRegistrationRequest;

import java.util.List;

public interface AuctionParticipantService {
    List<AuctionParticipantResponse> getAllAuctionParticipants();
    String register(Integer itemId, AuctionParticipantRequest request);
    String cancelRegistration(Integer itemId, CancelRegistrationRequest request);
    AuctionParticipantResponse refundDeposit(String registrationId);
}
