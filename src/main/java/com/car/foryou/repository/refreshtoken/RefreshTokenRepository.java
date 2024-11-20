package com.car.foryou.repository.refreshtoken;

import com.car.foryou.model.RefreshToken;
import com.car.foryou.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);

    RefreshToken findByUser(User user);
}
