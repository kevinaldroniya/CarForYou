package com.car.foryou.repository.auctionparticipant;

import com.car.foryou.model.AuctionParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuctionParticipantRepository extends JpaRepository<AuctionParticipant, Integer> {
    Optional<AuctionParticipant> findByItemIdAndParticipantId(Integer itemId, Integer participantId);
    List<AuctionParticipant> findByItemId(Integer itemId);
}
