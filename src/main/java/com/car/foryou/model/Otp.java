package com.car.foryou.model;

import com.car.foryou.dto.otp.OtpType;
import com.car.foryou.model.baseattribute.BaseModel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "otp")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Otp extends BaseModel {

    @Column(name = "otp_number")
    private Integer otpNumber;

    @Column(name = "otp_expiration")
    private Long otpExpiration;

    @Column(name = "otp_type")
    @Enumerated(EnumType.STRING)
    private OtpType otpType;

    @Column(name = "is_used")
    private Boolean isUsed;

    @ManyToOne
    private User user;

    @PrePersist
    public void onGenerate(){
        isUsed = false;
    }
}
