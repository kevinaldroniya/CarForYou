package com.car.foryou.repository.auction;

import com.car.foryou.model.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction, Integer> {
    List<Auction> findAllByItemId(Integer itemId);
}
