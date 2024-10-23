package com.car.foryou.repository.bid;

import com.car.foryou.model.BidDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BidDetailRepository extends JpaRepository<BidDetail, Integer> {
    List<BidDetail> findAllByItemId(Integer itemId);

    @Query(
            value = "SELECT * FROM bid_detail WHERE item_id = ?1 ORDER BY total_bid DESC LIMIT 1",
            nativeQuery = true
    )
    Optional<BidDetail> findByItemIdOrderByTotalBidDesc(Integer itemId);
}
