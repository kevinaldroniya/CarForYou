package com.car.foryou.repository.bid;

import com.car.foryou.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Integer> {

    @Query(
            "SELECT b " +
                    "FROM Bid b " +
                    "WHERE b.auction.id = :auctionId "
    )
    List<Bid> findByAuctionId(Integer auctionId);
}
