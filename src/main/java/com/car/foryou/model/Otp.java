package com.car.foryou.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "otp")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "otp_number")
    private Integer otpNumber;

    @Column(name = "otp_expiration")
    private Long otpExpiration;

    @OneToOne
    private User user;


}