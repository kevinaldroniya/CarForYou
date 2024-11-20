package com.car.foryou.model;

import com.car.foryou.dto.payment.PaymentMethod;
import com.car.foryou.dto.payment.PaymentStatus;
import com.car.foryou.dto.payment.PaymentType;
import com.car.foryou.model.baseattribute.BaseModel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "payment")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Payment extends BaseModel {
    @Column(name = "payment_amount")
    private Long paymentAmount;

    @Column(name = "payment_time")
    private Instant paymentTime;

    @Column(name = "payment_proof")
    private String paymentProof;

    @Column(name = "payment_expiration")
    private Instant paymentExpiration;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "order_id")
    private String orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    private PaymentType paymentType;

    @Column(name = "payment_code")
    private String paymentCode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @PreUpdate
    public void onUpdatePayment(){
        paymentTime = Instant.now();
    }
}
