package com.car.foryou.repository.item;

import com.car.foryou.dto.item.ItemFilterRequest;
import com.car.foryou.model.Item;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ItemSpecifications {

    private ItemSpecifications() {
    }

    public static Specification<Item> hasAnyKeywords(ItemFilterRequest filterRequest){
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (!filterRequest.getBrand().isEmpty()){
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("brand"), filterRequest.getBrand()));
            }

            if (!filterRequest.getModel().isEmpty()){
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("model"), filterRequest.getModel()));
            }

            if (!filterRequest.getVariant().isEmpty()){
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("variant"), filterRequest.getVariant()));
            }

            if (filterRequest.getYear() != 0){
                predicate = criteriaBuilder.and(criteriaBuilder.equal(root.get("year"), filterRequest.getYear()));
            }

            if (filterRequest.getSearch().isEmpty()){
                return predicate;
            }

            String[] keywords = filterRequest.getSearch().split(" ");
            List<Predicate> predicateList = new ArrayList<>();

            for (String keyword : keywords){
                Predicate brand = criteriaBuilder.like(root.get("brand"), "%" + keyword + "%");
                Predicate model = criteriaBuilder.like(root.get("model"), "%" + keyword + "%");
                Predicate variant = criteriaBuilder.like(root.get("variant"), "%" + keyword + "%");
                Predicate color = criteriaBuilder.like(root.get("color"), "%" + keyword + "%");
                Predicate licensePlat = criteriaBuilder.like(root.get("licensePlat"), "%" + keyword + "%");
                Predicate year = criteriaBuilder.like(root.get("year").as(String.class), "%" + keyword + "%");
                predicateList.add(criteriaBuilder.or(brand, model, variant, year, color, licensePlat));
            }
            Predicate searchPredicate = criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
            predicate = criteriaBuilder.and(predicate, searchPredicate);
            return predicate;
        };

    }
}
