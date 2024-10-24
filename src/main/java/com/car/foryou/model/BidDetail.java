package com.car.foryou.model;

import com.car.foryou.dto.bid.BidStatus;
import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.checker.units.qual.C;

import java.time.Instant;

@Entity
@Table(name = "bid_detail")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BidDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "bidder_id")
    private User bidder;

    @Column(name = "item_id")
    private Integer itemId;

    @Column(name = "total_bid")
    private Long totalBid;

    @Column(name = "bid_time")
    private Instant bidTime;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BidStatus status;

    @PrePersist
    public void prePersist() {
        this.bidTime = Instant.now();
    }
}
