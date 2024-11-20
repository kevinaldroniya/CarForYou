package com.car.foryou.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Table(name = "participant")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Participant {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "registration_date")
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id")
    private Auction auction;

    @Enumerated(EnumType.STRING)
    @Column(name = "deposit_status")
    private DepositStatus depositStatus;

    @Column(name = "highest_bid")
    private Long highestBid;

    @Enumerated(EnumType.STRING)
    @Column(name = "auction_process_status")
    private AuctionProcessStatus auctionProcessStatus;

    @Column(name = "payment_expiry")
    private Instant paymentExpiry;

    @PrePersist
    public void onPersists(){
        this.createdAt = Instant.now();
    }

    public enum DepositStatus{
        UNPAID,
        PAID,
        CANCELED,
        PENALIZED,
        WINNER;
    }

    public enum AuctionProcessStatus{
        PENDING_CONFIRMATION,
        CONFIRMATION_CANCELED,
        PAYMENT_PENDING,
        PAYMENT_COMPLETED,
        PAYMENT_CANCELED
    }
}



