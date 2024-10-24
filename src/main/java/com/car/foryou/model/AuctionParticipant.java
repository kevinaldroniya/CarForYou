package com.car.foryou.model;

import com.car.foryou.dto.auctionparticipant.AuctionRegistrationStatus;
import com.car.foryou.dto.payment.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Table
@Entity(name = "auction_participant")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuctionParticipant {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "itemId")
    private Integer itemId;

    @Column(name = "deposit_amount")
    private Integer depositAmount;

    @Column(name = "registration_date")
    private Instant registrationDate;

    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "registration_status")
    @Enumerated(EnumType.STRING)
    private AuctionRegistrationStatus registrationStatus;

    @Column(name = "cancel_reason")
    private String cancelReason;

    @Column(name = "cancel_time")
    private Instant cancelTime;

    @ManyToOne
    @JoinColumn(name = "participant_id")
    private User participant;

    @PrePersist
    public void onPersists(){
        this.registrationDate = Instant.now();
    }

    @PreUpdate
    public void onUpdate(){
        if (registrationStatus.equals(AuctionRegistrationStatus.CANCELLED)){
            this.cancelTime = Instant.now();
        }
    }
}
