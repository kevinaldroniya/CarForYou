package com.car.foryou.controller;

import com.car.foryou.dto.GeneralResponse;
import com.car.foryou.dto.bid.BidDetailResponse;
import com.car.foryou.dto.bid.BidUpdateRequest;
import com.car.foryou.service.bid.BidService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bids")
public class BidController  {

    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @GetMapping("/{bidId}")
    public ResponseEntity<BidDetailResponse> getBidResponseById(@PathVariable("bidId") Integer id){
        return ResponseEntity.ok(bidService.getBidDetailResponseById(id));
    }

    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping("/{auctionId}")
    public ResponseEntity<GeneralResponse<String>> placeBid(@PathVariable("auctionId") Integer auctionId){
        return ResponseEntity.ok(bidService.placeBid(auctionId));
    }

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<List<BidDetailResponse>> getAllBidDetailByItemId(@PathVariable("auctionId") Integer auctionId){
        return ResponseEntity.ok(bidService.getAllBidsByAuctionId(auctionId));
    }

//    @GetMapping("highest/{itemId}")
//    public ResponseEntity<List<Long>> getHighestBid(@PathVariable("itemId") Integer itemId){
//        return ResponseEntity.ok(bidService.getHighestBid(itemId));
//    }
//
//    @GetMapping("winner/{itemId}")
//    public ResponseEntity<List<BidDetailResponse>> getAuctionWinner(@PathVariable("itemId") Integer itemId){
//        return ResponseEntity.ok(bidService.getAuctionWinner(itemId));
//    }

}
