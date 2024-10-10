package com.car.foryou.repository;

import com.car.foryou.model.AuctionParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuctionParticipantRepository extends JpaRepository<AuctionParticipant, String> {
    Optional<AuctionParticipant> findByItemIdAndUserId(Integer itemId, Integer userId);
}
