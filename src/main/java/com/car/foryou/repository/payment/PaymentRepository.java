package com.car.foryou.repository.payment;

import com.car.foryou.model.PaymentDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentDetail, Integer> {
    Optional<PaymentDetail> findByUserIdAndItemId(Integer user, Integer item);
}
