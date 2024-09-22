package com.car.foryou.repository;

import com.car.foryou.model.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {

    @Query(
            value = "SELECT * FROM `group` g WHERE :name IS NULL OR :name = '' " +
                    "OR LOWER(g.name) LIKE LOWER(CONCAT('%', :name, '%'))",
            nativeQuery = true
    )
    Page<Group> findAllByFilter(@Param("name") String name, Pageable pageable);
    Optional<Group> findByName(String name);
}
