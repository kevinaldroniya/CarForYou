package com.car.foryou.repository;

import com.car.foryou.dto.variant.VariantCriteria;
import com.car.foryou.exception.ConversionException;
import com.car.foryou.model.CarModel;
import com.car.foryou.model.Variant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
public class VariantSpecifications {

    private VariantSpecifications(){}

    private static final String JSON_CONTAINS = "JSON_CONTAINS";
    private static final String STRING = "String";
    private static final String JSON_STRING = "JSON String";
    /*
        * This method is responsible for creating a Specification object.
        * The Specification object is used to create a Predicate object.
        * The Predicate object is used to create a query to get the list of variants.
        * The query is used to get the list of variants based on the criteria.
     */
    public static Specification<Variant> hasCriteria(VariantCriteria criteria){
        /*
            * root is used to get the root of the query.
            * query is used to create the query.
            * criteriaBuilder is used to create the criteria.
         */
        return (root, query, criteriaBuilder) -> {
            /*
               * ObjectMapper is used to convert the criteria object to a JSON string.
             */
            ObjectMapper objectMapper = new ObjectMapper();
            /*
             * predicate is used to create the query.
             * modelJoin is used to join the Variant and CarModel tables.
             */
            Predicate predicate = criteriaBuilder.conjunction();
            Join<Variant, CarModel> modelJoin = root.join("carModel");

            try {
                if (!criteria.getEngine().isEmpty()){
                    // Convert the engine list to a JSON string
                    String engine = objectMapper.writeValueAsString(criteria.getEngine());

                    // Create a query to get the list of variants based on the engine
                    predicate = criteriaBuilder.and(
                            predicate, criteriaBuilder.equal(
                                    criteriaBuilder.function(
                                            JSON_CONTAINS, Boolean.class, root.get("engine"), criteriaBuilder.literal(engine)
                                    ),
                                    true
                            ));
                }

                if (!criteria.getTransmission().isEmpty()){
                    String transmission = objectMapper.writeValueAsString(criteria.getTransmission());

                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(
                            criteriaBuilder.function(
                                    JSON_CONTAINS, Boolean.class, root.get("transmission"), criteriaBuilder.literal(transmission)
                            ),
                            true
                    ));
                }

                if (!criteria.getFuelType().isEmpty()){
                    String fuelType = objectMapper.writeValueAsString(criteria.getFuelType());

                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(
                            criteriaBuilder.function(
                                    JSON_CONTAINS, Boolean.class, root.get("fuel"), criteriaBuilder.literal(fuelType)
                            ),
                            true
                    ));
                }
            }catch (JsonProcessingException e){
                throw new ConversionException(STRING, JSON_STRING);
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
        };
    }
}
