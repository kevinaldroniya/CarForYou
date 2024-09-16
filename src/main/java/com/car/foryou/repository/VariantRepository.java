package com.car.foryou.repository;

import com.car.foryou.model.Variant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VariantRepository extends JpaRepository<Variant, Long> {
    Optional<Variant> findByNameAndYear(String name, int year);
}
