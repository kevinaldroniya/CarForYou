package com.car.foryou.service.auction;

import com.car.foryou.dto.auction.AuctionStatus;
import com.car.foryou.dto.item.ItemStatus;
import com.car.foryou.exception.GeneralException;
import com.car.foryou.exception.InvalidRequestException;
import com.car.foryou.exception.ResourceNotFoundException;
import com.car.foryou.model.Auction;
import com.car.foryou.model.Item;
import com.car.foryou.repository.auction.AuctionRepository;
import com.car.foryou.service.item.ItemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService{

    private final AuctionRepository auctionRepository;
    private final ItemService itemService;

    private static final String AUCTION = "Auction";


    @Override
    public List<Auction> getAllAuctions() {
        return auctionRepository.findAll();
    }

    @Override
    public List<Auction> getAuctionByItemId(Integer itemId) {
        return auctionRepository.findAllByItemId(itemId);
    }

//    @Override
//    public Auction getAuctionByAuctionIdAndUserId(Integer auctionId, Integer userId) {
//        return auctionRepository.findByIdAndUserId(auctionId, userId).orElseThrow(
//                () -> new ResourceNotFoundException(AUCTION, "userId", userId)
//        );
//    }

    @Override
    public Auction getAuctionById(Integer auctionId) {
        return auctionRepository.findById(auctionId).orElseThrow(
                () -> new ResourceNotFoundException(AUCTION, "ID", auctionId)
        );
    }

    @Transactional
    @Override
    public Auction createAuction(Integer itemId, String startDate, String endDate) {
        Item item = itemService.getItemById(itemId);
        if (!ItemStatus.AVAILABLE.equals(item.getStatus())){
            throw new InvalidRequestException("Item is not available for auction", HttpStatus.BAD_REQUEST);
        }
        getAuctionByItemId(itemId).forEach(auction -> {
            if (AuctionStatus.ACTIVE.equals(auction.getStatus())){
                throw new InvalidRequestException("Item is already in auction", HttpStatus.BAD_REQUEST);
            }
        });
        ZonedDateTime start = ZonedDateTime.parse(startDate);
        ZonedDateTime end = ZonedDateTime.parse(endDate);
        validateAuctionTime(start, end);
        Integer deposit = (int) (item.getStartingPrice() * 0.1);
        Auction auction = Auction.builder()
                .item(item)
                .depositAmount(deposit)
                .startDate(start.toInstant())
                .endDate(end.toInstant())
                .status(AuctionStatus.ACTIVE)
                .build();
        Auction saved = auctionRepository.save(auction);
        if (!AuctionStatus.ACTIVE.equals(saved.getStatus())){
            throw new GeneralException("Failed to create auction", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        itemService.updateItemStatus(item.getId(), ItemStatus.AUCTION_SCHEDULED);
        return saved;
    }

    private void validateAuctionTime(ZonedDateTime start, ZonedDateTime end) {
        if (start.isBefore(ZonedDateTime.now().plusDays(1))){
            throw new InvalidRequestException("Auction must be start at least 24 from now", HttpStatus.BAD_REQUEST);
        }
        if (end.isBefore(start.plusDays(1))){
            throw new InvalidRequestException("Auction must be end at lease 24 after the auction start", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public Auction updateAuction(Integer auctionId, String startDate, String endDate) {
        Auction auction = getAuctionById(auctionId);
        validateAuction(auction);
        ZonedDateTime start = ZonedDateTime.parse(startDate);
        ZonedDateTime end = ZonedDateTime.parse(endDate);
        validateAuctionTime(start, end);
        auction.setStartDate(start.toInstant());
        auction.setEndDate(end.toInstant());
        return auctionRepository.save(auction);
    }

    private void validateAuction(Auction auction) {
        if (auction.getStartDate().isBefore(Instant.now().plus(1, ChronoUnit.DAYS))){
            throw new InvalidRequestException("Auction cannot be modified, because it starts in less than 24 hours", HttpStatus.BAD_REQUEST);
        }

        if (!auction.getStatus().equals(AuctionStatus.ACTIVE)){
            throw new InvalidRequestException("Auction cannot be modified, because it is not active", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public Auction cancelAuction(Integer auctionId) {
        Auction auction = getAuctionById(auctionId);
        validateAuction(auction);
        auction.setStatus(AuctionStatus.CANCELLED);
        Auction saved = auctionRepository.save(auction);
        itemService.updateItemStatus(auction.getItem().getId(), ItemStatus.AVAILABLE);
        return saved;
    }
}
