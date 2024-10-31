package com.car.foryou.model;

import com.car.foryou.dto.bid.BidStatus;
import com.car.foryou.model.baseattribute.BaseModel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Entity
@Table(name = "bid_detail")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class BidDetail extends BaseModel {
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
