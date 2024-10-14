package com.car.foryou.controller;

import com.car.foryou.dto.auctionparticipant.AuctionParticipantRequest;
import com.car.foryou.dto.auctionparticipant.AuctionParticipantResponse;
import com.car.foryou.dto.auctionparticipant.CancelRegistrationRequest;
import com.car.foryou.service.auctionparticipant.AuctionParticipantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auctionParticipants")
public class AuctionParticipantController {
    private final AuctionParticipantService auctionParticipantService;

    public AuctionParticipantController(AuctionParticipantService auctionParticipantService) {
        this.auctionParticipantService = auctionParticipantService;
    }

    @PostMapping("/register/{itemId}")
    public ResponseEntity<String> register(@PathVariable("itemId") Integer itemId, @RequestBody AuctionParticipantRequest request){
        return new ResponseEntity<>(auctionParticipantService.register(itemId, request), HttpStatus.CREATED);
    }

    @PutMapping("/cancelRegistration/{itemId}")
    public ResponseEntity<String> cancelRegistration(@PathVariable("itemId") Integer itemId, @Valid @RequestBody CancelRegistrationRequest request){
        return ResponseEntity.ok(auctionParticipantService.cancelRegistration(itemId, request));
    }

    @PostMapping("/refundDeposit/{registrationId}")
    public ResponseEntity<AuctionParticipantResponse> refundDeposit(@PathVariable("registrationId") String registrationId){
        return ResponseEntity.ok(auctionParticipantService.refundDeposit(registrationId));
    }
}
