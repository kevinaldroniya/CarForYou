package com.car.foryou.repository.item;

import com.car.foryou.dto.item.ItemStatus;
import com.car.foryou.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer>, JpaSpecificationExecutor<Item> {
    Optional<Item> findByIdAndStatus(Integer id, ItemStatus status);
}
