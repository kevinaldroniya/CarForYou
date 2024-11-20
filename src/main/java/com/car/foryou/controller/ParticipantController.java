package com.car.foryou.controller;

import com.car.foryou.dto.participant.ParticipantResponse;
import com.car.foryou.model.Participant;
import com.car.foryou.service.participant.ParticipantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/participants")
public class ParticipantController {
    private final ParticipantService participantService;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<List<Participant>> getAllParticipantsByAuctionId(@PathVariable("auctionId") Integer auctionId){
        return ResponseEntity.ok(participantService.getParticipantByAuctionId(auctionId));
    }


    @GetMapping("/{participantId}")
    public ResponseEntity<ParticipantResponse> getParticipantById(@PathVariable("participantId") Integer participantId){
        return ResponseEntity.ok(participantService.getParticipantResponseV2(participantId));
    }

    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping("/auction/{auctionId}")
    public ResponseEntity<ParticipantResponse> createParticipant(@PathVariable("auctionId") Integer auctionId){
        ParticipantResponse participant = participantService.createParticipant(auctionId);
        return new ResponseEntity<>(participant, HttpStatus.CREATED);
    }
//    @PreAuthorize("hasAnyRole('AUCTIONEER', 'ADMIN')")
//    @GetMapping
//    public ResponseEntity<List<ParticipantResponse>> getAllAuctionParticipants(){
//        return ResponseEntity.ok(participantService.getAllAuctionParticipants());
//    }
//
//    @PreAuthorize("hasAnyRole('AUCTIONEER', 'ADMIN')")
//    @PostMapping
//    public ResponseEntity<ParticipantResponse> updateParticipant(AuctionParticipantUpdateRequest request){
//        return ResponseEntity.ok(participantService.updateParticipant(request));
//    }
//
//    @PreAuthorize("hasAnyRole('AUCTIONEER', 'ADMIN')")
//    @GetMapping("/{participantId}")
//    public ResponseEntity<ParticipantResponse> getParticipantById(@PathVariable("participantId") Integer participantId){
//        return ResponseEntity.ok(participantService.getParticipantResponseById(participantId));
//    }
//
//    @PreAuthorize("hasAnyRole('USER')")
//    @PostMapping("/register/{itemId}")
//    public ResponseEntity<GeneralResponse<ParticipantResponse>> register(@PathVariable("itemId") Integer itemId, @RequestBody AuctionParticipantRequest request){
//        return new ResponseEntity<>(participantService.register(itemId, request), HttpStatus.CREATED);
//    }
//
//    @PreAuthorize("hasAnyRole('USER')")
//    @PostMapping("/cancelRegistration/{itemId}")
//    public ResponseEntity<GeneralResponse<ParticipantResponse>> cancelRegistration(@PathVariable("itemId") Integer itemId, @Valid @RequestBody AuthParticipantCancelRequest request){
//        return ResponseEntity.ok(participantService.cancelRegistration(itemId, request));
//    }
//
//    @PreAuthorize("hasAnyRole('AUCTIONEER', 'ADMIN')")
//    @PostMapping(
//            path = "/refundDeposit/{registrationId}",
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    public ResponseEntity<GeneralResponse<ParticipantResponse>> refundDeposit(@PathVariable("registrationId") Integer registrationId){
//        return new ResponseEntity<>(participantService.refundDeposit(registrationId), HttpStatus.OK);
//    }
//
//    @PreAuthorize("hasAnyRole('AUCTIONEER', 'ADMIN')")
//    @PostMapping("/bulkRefund/{itemId}")
//    public ResponseEntity<GeneralResponse<ParticipantResponse>> bulkRefund(@PathVariable("itemId") Integer itemId){
//        return ResponseEntity.ok(participantService.bulkRefundDeposit(itemId));
//    }
//
//    @PreAuthorize("hasAnyRole('AUCTIONEER', 'ADMIN')")
//    @GetMapping("/item/{itemId}")
//    public ResponseEntity<List<ParticipantResponse>> getParticipantByItemId(@PathVariable("itemId") Integer itemId){
//        return ResponseEntity.ok(participantService.getParticipantResponseByItemId(itemId));
//    }
}
