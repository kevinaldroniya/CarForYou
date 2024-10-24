package com.car.foryou.controller;

import com.car.foryou.dto.auctionparticipant.AuctionParticipantRefunded;
import com.car.foryou.dto.auctionparticipant.AuctionParticipantRequest;
import com.car.foryou.dto.auctionparticipant.AuctionParticipantResponse;
import com.car.foryou.dto.auctionparticipant.AuthParticipantCancelRequest;
import com.car.foryou.service.auctionparticipant.AuctionParticipantService;
import com.twilio.twiml.fax.Receive;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    public ResponseEntity<String> cancelRegistration(@PathVariable("itemId") Integer itemId, @Valid @RequestBody AuthParticipantCancelRequest request){
        return ResponseEntity.ok(auctionParticipantService.cancelRegistration(itemId, request));
    }

    @PostMapping(
            path = "/refundDeposit",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> refundDeposit(@RequestBody String registrationId){
        String auctionParticipantResponse = auctionParticipantService.refundDeposit(registrationId);
        return new ResponseEntity<>(auctionParticipantResponse, HttpStatus.OK);
    }
}
