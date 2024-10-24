package com.car.foryou.repository.payment;

import com.car.foryou.model.Item;
import com.car.foryou.model.PaymentDetail;
import com.car.foryou.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentDetail, Integer> {
    Optional<PaymentDetail> findByUserAndItem(User user, Item item);
}
