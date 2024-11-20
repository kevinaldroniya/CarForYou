package com.car.foryou.repository.participant;

import com.car.foryou.model.Participant;
import feign.Param;
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
            "WHERE p.auction.id = :auctionId "
    )
    List<Participant> findAllByAuctionId(Integer auctionId);
}
