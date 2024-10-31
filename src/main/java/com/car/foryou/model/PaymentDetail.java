package com.car.foryou.model;

import com.car.foryou.dto.payment.BankAccount;
import com.car.foryou.dto.payment.PaymentMethod;
import com.car.foryou.dto.payment.PaymentStatus;
import com.car.foryou.model.baseattribute.BaseModel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "payment_detail")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class PaymentDetail extends BaseModel {
    @Column(name = "payment_amount")
    private Long paymentAmount;

    @Column(name = "payment_time")
    private Instant paymentTime;

    @Column(name = "bank_account")
    private BankAccount bankAccount;

    @Column(name = "payment_proof")
    private String paymentProof;

//    @Column(name = "shipping_address")
//    private String shippingAddress;
//
//    @Column(name = "shipping_city")
//    private String shippingCity;
//
//    @Column(name = "shipping_province")
//    private String shippingProvince;
//
//    @Column(name = "shipping_postal_code")
//    private String shippingPostalCode;

    @Column(name = "payment_expiration")
    private Instant paymentExpiration;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "source_bank")
    private String sourceBank;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @PrePersist
    public void onCreate(){
        paymentExpiration = Instant.now().plus(1, ChronoUnit.DAYS);
    }

    @PreUpdate
    public void onUpdatePayment(){
        paymentTime = Instant.now();
    }
}
