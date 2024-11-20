package com.car.foryou.model;

import com.car.foryou.dto.auction.AuctionStatus;
import com.car.foryou.service.user.CustomUserDetailService;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "auction")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Column(name = "deposit_amount")
    private Integer depositAmount;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "createdBy")
    private Integer createdBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AuctionStatus status;

    @PrePersist
    public void onCreate(){
        this.createdBy = CustomUserDetailService.getLoggedInUserDetails().getId();
        this.createdAt = Instant.now();
    }
}
