package com.car.foryou.controller;

import com.car.foryou.dto.auctionparticipant.AuctionParticipantRequest;
import com.car.foryou.dto.auctionparticipant.AuctionParticipantResponse;
import com.car.foryou.dto.auctionparticipant.AuthParticipantCancelRequest;
import com.car.foryou.service.auctionparticipant.AuctionParticipantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auctionParticipants")
public class AuctionParticipantController {
    private final AuctionParticipantService auctionParticipantService;

    public AuctionParticipantController(AuctionParticipantService auctionParticipantService) {
        this.auctionParticipantService = auctionParticipantService;
    }

    @GetMapping
    public ResponseEntity<List<AuctionParticipantResponse>> getAllAuctionParticipants(){
        return ResponseEntity.ok(auctionParticipantService.getAllAuctionParticipants());
    }

    @GetMapping("/{participantId}")
    public ResponseEntity<AuctionParticipantResponse> getParticipantById(@PathVariable("participantId") Integer participantId){
        return ResponseEntity.ok(auctionParticipantService.getParticipantById(participantId));
    }

    @PostMapping("/register/{itemId}")
    public ResponseEntity<String> register(@PathVariable("itemId") Integer itemId, @RequestBody AuctionParticipantRequest request){
        return new ResponseEntity<>(auctionParticipantService.register(itemId, request), HttpStatus.CREATED);
    }

    @PutMapping("/cancelRegistration/{itemId}")
    public ResponseEntity<String> cancelRegistration(@PathVariable("itemId") Integer itemId, @Valid @RequestBody AuthParticipantCancelRequest request){
        return ResponseEntity.ok(auctionParticipantService.cancelRegistration(itemId, request));
    }

    @PostMapping(
            path = "/refundDeposit/{registrationId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> refundDeposit(@PathVariable("registrationId") Integer registrationId){
        String auctionParticipantResponse = auctionParticipantService.refundDeposit(registrationId);
        return new ResponseEntity<>(auctionParticipantResponse, HttpStatus.OK);
    }

    @PostMapping("/bulkRefund/{itemId}")
    public ResponseEntity<String> bulkRefund(@PathVariable("itemId") Integer itemId){
        return ResponseEntity.ok(auctionParticipantService.bulkRefundDeposit(itemId));
    }
}
