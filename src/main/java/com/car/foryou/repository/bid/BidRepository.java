package com.car.foryou.repository.bid;

import com.car.foryou.model.Bid;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Integer> {

    @Query(
            "SELECT b " +
                    "FROM Bid b " +
                    "WHERE b.auction.id = :auctionId "
    )
    List<Bid> findByAuctionId(Integer auctionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT MAX(b.bidAmount)
            FROM Bid b
            WHERE b.auction.id = :auctionId
            """)
    Optional<Long> findMaxBidAmountByAuctionId(Integer auctionId);


//    @Query(value = """
//            SELECT b
//            FROM Bid b
//            WHERE b.auction.id = :auctionId
//            ORDER BY b.id DESC
//            LIMIT 1
//            FOR UPDATE
//            """, nativeQuery = true)
    @Query(value = "SELECT * FROM bid WHERE auction_id = :auctionId ORDER BY id DESC LIMIT 1 FOR UPDATE", nativeQuery = true)
    Optional<Bid> findHighestBidByAuctionId(Integer auctionId);
}
