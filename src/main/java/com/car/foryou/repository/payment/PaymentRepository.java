package com.car.foryou.repository.payment;

import com.car.foryou.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
//    Optional<Payment> findByUserIdAndItemId(Integer user, Integer item);

    @Query("""
            SELECT p
            FROM Payment p
            JOIN FETCH p.user u
            WHERE p.order_id = :orderId
            """)
    Optional<Payment> findByOrderId(String orderId);
}
