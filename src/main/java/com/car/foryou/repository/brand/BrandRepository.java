package com.car.foryou.repository.brand;

import com.car.foryou.model.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
    Optional<Brand> findByName(String name);

    @Query(
            "SELECT b " +
                    "FROM Brand b " +
                    "WHERE :name IS NULL " +
                    "OR :name = '' " +
                    "OR LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%'))"
    )
    Page<Brand> findAll(@Param("name") String name, Pageable pageable);

}
