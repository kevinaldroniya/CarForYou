package com.car.foryou.model;

import com.car.foryou.dto.bid.BidStatus;
import com.car.foryou.model.baseattribute.BaseModel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Entity
@Table(name = "bid")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "bid_amount")
    private Long bidAmount;

    @Column(name = "bid_time")
    private Instant bidTime;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BidStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id")
    private Auction auction;

    @Version
    private Long version;

    @PrePersist
    public void prePersist() {
        this.bidTime = Instant.now();
    }
}
