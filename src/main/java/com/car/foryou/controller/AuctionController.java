package com.car.foryou.controller;

import com.car.foryou.dto.auction.AuctionCreateRequest;
import com.car.foryou.model.Auction;
import com.car.foryou.service.auction.AuctionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @GetMapping
    public ResponseEntity<List<Auction>> getAllAuctions(){
        return ResponseEntity.ok(auctionService.getAllAuctions());
    }

    @GetMapping("/{auctionId}")
    public ResponseEntity<Auction> getAuctionById(@PathVariable("auctionId") Integer auctionId){
        return ResponseEntity.ok(auctionService.getAuctionById(auctionId));
    }

    @GetMapping("item/{itemId}")
    public ResponseEntity<List<Auction>> getAuctionByItemId(@PathVariable("itemId") Integer itemId){
        return ResponseEntity.ok(auctionService.getAuctionByItemId(itemId));
    }

    @PostMapping("item/{itemId}")
    public ResponseEntity<Auction> createAuction(@PathVariable("itemId") Integer itemId, @RequestBody AuctionCreateRequest request){
        Auction auction = auctionService.createAuction(itemId, request.getStartDate(), request.getEndDate());
        return new ResponseEntity<>(auction, HttpStatus.CREATED);
    }

    @PostMapping("/{auctionId}")
    public ResponseEntity<Auction> updateAuction(@PathVariable("auctionId") Integer auctionId, @RequestBody AuctionCreateRequest request){
        return new ResponseEntity<>(auctionService.updateAuction(auctionId, request.getStartDate(), request.getEndDate()), HttpStatus.CREATED);
    }

    @DeleteMapping("/{auctionId}")
    public ResponseEntity<Auction> cancelAuction(@PathVariable("auctionId") Integer auctionId){
        return ResponseEntity.ok(auctionService.cancelAuction(auctionId));
    }

    @PostMapping("/end/{auctionId}")
    public ResponseEntity<Auction> endAuction(@PathVariable("auctionId") Integer auctionId){
        return ResponseEntity.ok(auctionService.endAuction(auctionId));
    }
}
