package com.car.foryou.repository;

import com.car.foryou.model.Otp;
import com.car.foryou.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByOtpNumberAndUser(Integer otpNumber, User user);
}
