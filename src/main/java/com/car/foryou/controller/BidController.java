package com.car.foryou.controller;

import com.car.foryou.api.v1.BaseApiControllerV1;
import com.car.foryou.dto.GeneralResponse;
import com.car.foryou.dto.bid.BidDetailResponse;
import com.car.foryou.dto.bid.BidUpdateRequest;
import com.car.foryou.service.bid.BidService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bid")
public class BidController implements BaseApiControllerV1 {

    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @GetMapping("/{bidId}")
    public ResponseEntity<BidDetailResponse> getBidResponseById(@PathVariable("bidId") Integer id){
        return ResponseEntity.ok(bidService.getBidDetailResponseById(id));
    }

    @PostMapping()
    public ResponseEntity<BidDetailResponse> updateBidById(@RequestBody BidUpdateRequest request){
        return ResponseEntity.ok(bidService.updateBidDetail(request));
    }

    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping("/place/{itemId}")
    public ResponseEntity<GeneralResponse<String>> placeBid(@PathVariable("itemId") Integer itemId){
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
    public ResponseEntity<GeneralResponse<String>> sendWinnerConfirmation(@PathVariable("bidDetailId") Integer bidId){
        return ResponseEntity.ok(bidService.sendWinnerConfirmation(bidId));
    }

    @GetMapping("/confirm")
    public ResponseEntity<GeneralResponse<String>> confirm(@RequestParam(value = "signature", required = true) String signature){
        return ResponseEntity.ok(bidService.confirmBidWinner(signature));
    }

    @PostMapping("/penalty/{bidDetailId}")
    public ResponseEntity<GeneralResponse<String>> setPenalty(@PathVariable("bidDetailId") Integer bidDetailId){
        return ResponseEntity.ok(bidService.setPenalty(bidDetailId));
    }
}
