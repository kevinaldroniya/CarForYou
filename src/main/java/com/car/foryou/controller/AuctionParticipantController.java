package com.car.foryou.controller;

import com.car.foryou.api.v1.BaseApiControllerV1;
import com.car.foryou.dto.GeneralResponse;
import com.car.foryou.dto.auctionparticipant.AuctionParticipantRequest;
import com.car.foryou.dto.auctionparticipant.AuctionParticipantResponse;
import com.car.foryou.dto.auctionparticipant.AuctionParticipantUpdateRequest;
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
public class AuctionParticipantController implements BaseApiControllerV1 {
    private final AuctionParticipantService auctionParticipantService;

    public AuctionParticipantController(AuctionParticipantService auctionParticipantService) {
        this.auctionParticipantService = auctionParticipantService;
    }

    @GetMapping
    public ResponseEntity<List<AuctionParticipantResponse>> getAllAuctionParticipants(){
        return ResponseEntity.ok(auctionParticipantService.getAllAuctionParticipants());
    }

    @PostMapping
    public ResponseEntity<AuctionParticipantResponse> updateParticipant(AuctionParticipantUpdateRequest request){
        return ResponseEntity.ok(auctionParticipantService.updateParticipant(request));
    }

    @GetMapping("/{participantId}")
    public ResponseEntity<AuctionParticipantResponse> getParticipantById(@PathVariable("participantId") Integer participantId){
        return ResponseEntity.ok(auctionParticipantService.getParticipantResponseById(participantId));
    }

    @PostMapping("/register/{itemId}")
    public ResponseEntity<GeneralResponse<AuctionParticipantResponse>> register(@PathVariable("itemId") Integer itemId, @RequestBody AuctionParticipantRequest request){
        return new ResponseEntity<>(auctionParticipantService.register(itemId, request), HttpStatus.CREATED);
    }

    @PostMapping("/cancelRegistration/{itemId}")
    public ResponseEntity<GeneralResponse<AuctionParticipantResponse>> cancelRegistration(@PathVariable("itemId") Integer itemId, @Valid @RequestBody AuthParticipantCancelRequest request){
        return ResponseEntity.ok(auctionParticipantService.cancelRegistration(itemId, request));
    }

    @PostMapping(
            path = "/refundDeposit/{registrationId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<GeneralResponse<AuctionParticipantResponse>> refundDeposit(@PathVariable("registrationId") Integer registrationId){
        return new ResponseEntity<>(auctionParticipantService.refundDeposit(registrationId), HttpStatus.OK);
    }

    @PostMapping("/bulkRefund/{itemId}")
    public ResponseEntity<GeneralResponse<AuctionParticipantResponse>> bulkRefund(@PathVariable("itemId") Integer itemId){
        return ResponseEntity.ok(auctionParticipantService.bulkRefundDeposit(itemId));
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<AuctionParticipantResponse>> getParticipantByItemId(@PathVariable("itemId") Integer itemId){
        return ResponseEntity.ok(auctionParticipantService.getParticipantResponseByItemId(itemId));
    }
}
