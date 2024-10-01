package com.car.foryou.repository;

import com.car.foryou.dto.variant.VariantCriteria;
import com.car.foryou.model.CarModel;
import com.car.foryou.model.Item;
import com.car.foryou.model.Variant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class VariantSpecifications {

    public static Specification<Variant> hasCriteria(VariantCriteria criteria){
        return (root, query, criteriaBuilder) -> {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Predicate predicate = criteriaBuilder.conjunction();

                Join<Variant, CarModel> modelJoin = root.join("carModel");

                if (!criteria.getEngine().isEmpty()){
                    String engine = objectMapper.writeValueAsString(criteria.getEngine());
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(
                            criteriaBuilder.function(
                                    "JSON_CONTAINS", Boolean.class, root.get("engine"), criteriaBuilder.literal(engine)
                            ),
                            true
                    ));
                }

                if (!criteria.getTransmission().isEmpty()){
                    String transmission = objectMapper.writeValueAsString(criteria.getTransmission());
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(
                            criteriaBuilder.function(
                                    "JSON_CONTAINS", Boolean.class, root.get("transmission"), criteriaBuilder.literal(transmission)
                            ),
                            true
                    ));
                }

                if (!criteria.getFuelType().isEmpty()){
                    String fuelType = objectMapper.writeValueAsString(criteria.getFuelType());
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(
                            criteriaBuilder.function(
                                    "JSON_CONTAINS", Boolean.class, root.get("fuel"), criteriaBuilder.literal(fuelType)
                            ),
                            true
                    ));
                }

                if (!criteria.getName().isEmpty()){
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("name"), criteria.getName()));
                }

                if (criteria.getYear() != 0){
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("year"), criteria.getYear()));
                }

                if (!criteria.getModel().isEmpty()){
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(modelJoin.get("name"), criteria.getModel()));
                }

                return predicate;
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
