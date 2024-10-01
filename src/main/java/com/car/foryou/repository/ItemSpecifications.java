package com.car.foryou.repository;

import com.car.foryou.model.Brand;
import com.car.foryou.model.CarModel;
import com.car.foryou.model.Item;
import com.car.foryou.model.Variant;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ItemSpecifications {

    private ItemSpecifications() {
    }

    public static Specification<Item> hasAnyKeywords(String searchValue){
        return (root, query, criteriaBuilder) -> {
            if (searchValue == null || searchValue.trim().isEmpty()){
                return criteriaBuilder.conjunction();
            }

            Join<Item, Variant> variantJoin = root.join("variant");
            Join<Variant, CarModel> modelJoin = variantJoin.join("carModel");
            Join<CarModel, Brand> brandJoin = modelJoin.join("brand");
            String[] keywords = searchValue.split(" ");
            List<Predicate> predicateList = new ArrayList<>();

            for (String keyword : keywords){
                Predicate brand = criteriaBuilder.like(brandJoin.get("name"), "%" + keyword + "%");
                Predicate model = criteriaBuilder.like(modelJoin.get("name"), "%" + keyword + "%");
                Predicate variant = criteriaBuilder.like(variantJoin.get("name"), "%" + keyword + "%");
                Predicate color = criteriaBuilder.like(root.get("color"), "%" + keyword + "%");
                Predicate licensePlat = criteriaBuilder.like(root.get("licensePlat"), "%" + keyword + "%");
                Predicate year = criteriaBuilder.like(variantJoin.get("year").as(String.class), "%" + keyword + "%");
                predicateList.add(criteriaBuilder.or(brand, model, variant, year, color, licensePlat));
            }
            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        };

    }
}
