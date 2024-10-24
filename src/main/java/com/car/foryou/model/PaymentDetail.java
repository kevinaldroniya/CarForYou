package com.car.foryou.model;

import com.car.foryou.dto.payment.PaymentMethod;
import com.car.foryou.dto.payment.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "payment_detail")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PaymentDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "payment_amount")
    private Long paymentAmount;

    @Column(name = "payment_time")
    private Instant paymentTime;

    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @Column(name = "shipping_address")
    private String shippingAddress;

    @Column(name = "shipping_city")
    private String shippingCity;

    @Column(name = "shipping_province")
    private String shippingProvince;

    @Column(name = "shipping_postal_code")
    private String shippingPostalCode;

    @Column(name = "payment_expiration")
    private Instant paymentExpiration;

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
    public void onUpdate(){
        paymentTime = Instant.now();
    }
}
