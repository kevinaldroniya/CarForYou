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
    public ResponseEntity<List<ParticipantResponse>> getAllParticipantsByAuctionId(@PathVariable("auctionId") Integer auctionId){
        return ResponseEntity.ok(participantService.getParticipantResponseByAuctionId(auctionId));
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

    @PostMapping("/send-confirmation/auction/{auctionId}")
    public ResponseEntity<ParticipantResponse> sendConfirmationToParticipant(@PathVariable("auctionId") Integer auctionId){
        return ResponseEntity.ok(participantService.sendConfirmationToParticipant(auctionId));
    }

    @PostMapping("/confirm/{participantId}")
    public ResponseEntity<ParticipantResponse> confirmAuctionProcess(@PathVariable("participantId") Integer participantId){
        return ResponseEntity.ok(participantService.confirmTheAuction(participantId));
    }

    @PostMapping("/cancel/{participantId}")
    public ResponseEntity<ParticipantResponse> cancelAuctionProcess(@PathVariable("participantId") Integer participantId){
        return ResponseEntity.ok(participantService.cancelAuctionProcess(participantId));
    }

    @PostMapping("/penalty/{participantId}")
    public ResponseEntity<ParticipantResponse> setPenaltyToParticipant(@PathVariable("participantId") Integer participantId){
        return ResponseEntity.ok(participantService.setPenalty(participantId));
    }
}
