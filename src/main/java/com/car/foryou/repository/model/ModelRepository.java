package com.car.foryou.repository.model;

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

    @Query("SELECT c " +
            "FROM CarModel c " +
            "WHERE :name IS NULL " +
            "OR :name = '' " +
            "OR LOWER(c.name) LIKE(CONCAT('%', :name, '%'))")
    Page<CarModel> findByNameContaining(@Param("name") String name, Pageable pageable);
    Page<CarModel> findAllByBrandId(int brandId, Pageable pageable);

    @Query("SELECT " +
            "m " +
            "FROM " +
            "CarModel m " +
            "JOIN Brand b " +
            "ON m.brand = b " +
            "WHERE m.name = :name " +
            "AND b.name = :brandName")
    Optional<CarModel> findByNameAndBrandName(@Param("name") String name, @Param("brandName") String brandName);

    List<CarModel> findAllByBrand(Brand brand);
}
