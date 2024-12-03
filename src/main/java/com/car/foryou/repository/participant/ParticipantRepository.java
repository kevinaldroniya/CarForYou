package com.car.foryou.repository.participant;

import com.car.foryou.model.Participant;
import feign.Param;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Integer> {

    @Query(value = """
            SELECT p
            FROM Participant p
            JOIN FETCH p.auction a
            WHERE a.id = :auctionId
            AND p.user.id = :userId
            """)
    Optional<Participant> findByAuctionIdAndUserId(@Param("auctionId") Integer auctionId, @Param("userId") Integer userId);

    @Query(
            "SELECT p " +
            "FROM Participant p " +
            "WHERE p.auction.id = :auctionId " +
            "ORDER BY p.highestBid DESC"
    )
    List<Participant> findAllByAuctionId(Integer auctionId);

    @Query(value = """
            SELECT
            *
            FROM
            participant p
            WHERE p.auction_id = :auctionId
            AND (p.auction_process_status NOT IN ('CONFIRMATION_CANCELED', 'PAYMENT_CANCELED') OR p.auction_process_status IS NULL)
            ORDER BY p.highest_bid DESC
            LIMIT 1
            """, nativeQuery = true)
    Optional<Participant> findWinnerByAuctionId(Integer auctionId);

    @NotNull
    @Query(value = """
            SELECT p
            FROM Participant p
            JOIN FETCH p.auction a
            WHERE p.id = :participantId
            """)
    Optional<Participant> findById(@NotNull @Param("participantId") Integer participantId);
}
