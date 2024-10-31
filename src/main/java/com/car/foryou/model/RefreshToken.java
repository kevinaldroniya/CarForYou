package com.car.foryou.model;

import com.car.foryou.model.baseattribute.BaseModel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Entity
@Table(name = "refresh_token")
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken extends BaseModel {
    @Column(name = "token")
    private String token;

    @Column(name = "expiration_time")
    private Instant expirationTime;

    @OneToOne
    private User user;
}
