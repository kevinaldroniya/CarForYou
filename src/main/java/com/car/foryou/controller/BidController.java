package com.car.foryou.controller;

import com.car.foryou.dto.bid.BidDetailResponse;
import com.car.foryou.service.bid.BidService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bid")
public class BidController {

    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping("/place/{itemId}")
    public ResponseEntity<String> placeBid(@PathVariable("itemId") Integer itemId){
        return ResponseEntity.ok(bidService.placeBid(itemId));
    }

    @GetMapping("/all/{itemId}")
    public ResponseEntity<List<BidDetailResponse>> getAllBidDetailByItemId(@PathVariable("itemId") Integer itemId){
        return ResponseEntity.ok(bidService.getAllBidByItem(itemId));
    }

    @GetMapping("highest/{itemId}")
    public ResponseEntity<List<Long>> getHighestBid(@PathVariable("itemId") Integer itemId){
        return ResponseEntity.ok(bidService.getHighestBid(itemId));
    }

    @GetMapping("winner/{itemId}")
    public ResponseEntity<List<BidDetailResponse>> getAuctionWinner(@PathVariable("itemId") Integer itemId){
        return ResponseEntity.ok(bidService.getAuctionWinner(itemId));
    }

    @PreAuthorize("hasAnyRole('AUCTIONEER','ADMIN')")
    @PostMapping("/sendWinnerConfirmation/{bidDetailId}")
    public ResponseEntity<String> sendWinnerConfirmation(@PathVariable("bidDetailId") Integer bidId){
        return ResponseEntity.ok(bidService.sendWinnerConfirmation(bidId));
    }

    @GetMapping("/confirm/{encodeBidId}/{encodeEmail}/{encodeOtp}")
    public ResponseEntity<String> confirm(@PathVariable("encodeBidId") String encodeBidId, @PathVariable("encodeEmail") String encodeEmail, @PathVariable("encodeOtp") String encodeOtp){
        return ResponseEntity.ok(bidService.confirmBidWinner(encodeBidId, encodeEmail, encodeOtp));
    }

    @PostMapping("/penalty/{bidDetailId}")
    public ResponseEntity<BidDetailResponse> setPenalty(@PathVariable("bidDetailId") Integer bidDetailId){
        return ResponseEntity.ok(bidService.setPenalty(bidDetailId));
    }
}
