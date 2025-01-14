package com.car.foryou.service.auction;

import com.car.foryou.dto.auction.AuctionCreateRequest;
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
import java.util.Objects;

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

    @Override
    public Auction getAuctionById(Integer auctionId) {
        return auctionRepository.findById(auctionId).orElseThrow(
                () -> new ResourceNotFoundException(AUCTION, "ID", auctionId)
        );
    }

    @Transactional
    @Override
    public Auction createAuction(Integer itemId, AuctionCreateRequest request) {
        if (Objects.isNull(request.getDpPercent())){
            request.setDpPercent(5);
        }
        Item item = itemService.getItemById(itemId);
        if (!ItemStatus.AVAILABLE.equals(item.getStatus())){
            throw new InvalidRequestException("Item is not available for auction", HttpStatus.BAD_REQUEST);
        }
        getAuctionByItemId(itemId).forEach(auction -> {
            if (AuctionStatus.ACTIVE.equals(auction.getStatus())){
                throw new InvalidRequestException("Item is already in auction", HttpStatus.BAD_REQUEST);
            }
        });
        ZonedDateTime start = ZonedDateTime.parse(request.getStartDate());
        ZonedDateTime end = ZonedDateTime.parse(request.getEndDate());
        validateAuctionTime(start, end);
        Integer deposit = (int) (item.getStartingPrice() * (request.getDpPercent() * 0.01));
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

    @Override
    public Auction updateAuctionStatus(Integer auctionId, AuctionStatus status) {
        Auction auction = getAuctionById(auctionId);
        auction.setStatus(status);
        return auctionRepository.save(auction);
    }

    @Transactional
    @Override
    public Auction endAuction(Integer auctionId){
        Auction auction = getAuctionById(auctionId);
        AuctionStatus status = auction.getStatus();
        boolean isPaymentSuccess = status.equals(AuctionStatus.PAYMENT_SUCCESS);
        boolean isPaymentCanceled = status.equals(AuctionStatus.PAYMENT_CANCELED);

        if (!isPaymentSuccess && !isPaymentCanceled){
            throw new InvalidRequestException("You can't end this auction", HttpStatus.BAD_REQUEST);
        }

        if (isPaymentSuccess){
            itemService.updateItemStatus(auction.getItem().getId(), ItemStatus.SOLD);
        } else {
            itemService.updateItemStatus(auction.getItem().getId(), ItemStatus.AVAILABLE);
        }

        auction.setStatus(AuctionStatus.ENDED);
        return auctionRepository.save(auction);
    }

    @Override
    public void updateTopBid(Integer auctionId, Long topBid) {
//        Auction auction = auctionRepository.findByIdLock(auctionId).orElseThrow(() -> new ResourceNotFoundException(AUCTION, "ID", auctionId));
        Auction auction = getAuctionById(auctionId);
        auction.setTopBid(topBid);
        auctionRepository.save(auction);
    }
}