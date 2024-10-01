package com.car.foryou.repository;

import com.car.foryou.model.CarModel;
import com.car.foryou.model.Variant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VariantRepository extends JpaRepository<Variant, Integer>, JpaSpecificationExecutor<Variant> {
    Optional<Variant> findByNameAndYearAndCarModel(String name, int year, CarModel carModel);
    @Query(
            value = "SELECT " +
                    "DISTINCT " +
                    "v.year as year " +
                    " FROM variant v WHERE v.model_id = :id ",
            nativeQuery = true
    )
    Page<Long> findAllByCarModelId(int id, Pageable pageable);
    Page<Variant> findAllByCarModelIdAndYear(int id, int year, Pageable pageable);

    Optional<Variant> findByName(String name);

    List<Variant> findAllByCarModel(CarModel carModel);
}
