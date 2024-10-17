package com.car.foryou.model;

import com.car.foryou.dto.otp.OtpType;
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

    @Column(name = "otp_type")
    @Enumerated(EnumType.STRING)
    private OtpType otpType;

    @ManyToOne
    private User user;


}
