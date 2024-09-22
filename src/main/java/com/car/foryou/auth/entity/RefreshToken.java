package com.car.foryou.auth.entity;

import com.car.foryou.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Builder
@Getter
@Setter
public class RefreshToken {
    private Long id;
    private String refreshToken;
    private Instant expirationTime;

    @OneToOne
    private User user;
}
