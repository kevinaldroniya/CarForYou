package com.car.foryou.repository.otp;

import com.car.foryou.dto.otp.OtpType;
import com.car.foryou.model.Otp;
import com.car.foryou.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Integer> {
    Optional<Otp> findByOtpNumberAndUser(int otpNumber, User user);
    List<Otp> findAllByUserAndOtpType(User user, OtpType otpType);
    void deleteAllByUserAndOtpType(User user, OtpType otpType);
    Optional<Otp> findByUserAndOtpType(User user, OtpType otpType);
}
