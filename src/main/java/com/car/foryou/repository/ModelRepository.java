package com.car.foryou.repository;

import com.car.foryou.model.Brand;
import com.car.foryou.model.CarModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModelRepository extends JpaRepository<CarModel, Integer> {
    Optional<CarModel> findByName(String name);
    Page<CarModel> findByNameContaining(String name, Pageable pageable);
    Page<CarModel> findAllByBrandId(int brandId, Pageable pageable);

    @Query("SELECT m FROM CarModel m WHERE m.name = :name AND m.brand.name = :brandName")
    Optional<CarModel> findByNameAndBrandName(@Param("name") String name, @Param("brandName") String brandName);

    List<CarModel> findAllByBrand(Brand brand);
}
