package com.car.foryou.repository.auction;

import com.car.foryou.model.Auction;
import feign.Param;
import jakarta.persistence.LockModeType;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction, Integer> {
    List<Auction> findAllByItemId(Integer itemId);
}
