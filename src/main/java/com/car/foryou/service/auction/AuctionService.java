package com.car.foryou.service.auction;

import com.car.foryou.dto.auction.AuctionCreateRequest;
import com.car.foryou.dto.auction.AuctionStatus;
import com.car.foryou.model.Auction;

import java.util.List;

public interface AuctionService {
    List<Auction> getAllAuctions();
    List<Auction> getAuctionByItemId(Integer itemId);
//    Auction getAuctionByAuctionIdAndUserId(Integer auctionId, Integer userId);
    Auction getAuctionById(Integer auctionId);
    Auction createAuction(Integer itemId, AuctionCreateRequest request);
    Auction updateAuction(Integer auctionId, String startDate, String endDate);
    Auction cancelAuction(Integer auctionId);
    Auction updateAuctionStatus(Integer auctionId, AuctionStatus status);
    Auction endAuction(Integer auctionId);
    void updateTopBid(Integer auctionId, Long topBid);
}
