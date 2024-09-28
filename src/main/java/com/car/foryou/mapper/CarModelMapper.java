package com.car.foryou.mapper;

import com.car.foryou.dto.model.CarModelFilterResponse;
import com.car.foryou.dto.model.CarModelRequest;
import com.car.foryou.dto.model.CarModelResponse;
import com.car.foryou.model.CarModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class CarModelMapper {

    private final ObjectMapper objectMapper;

    public CarModelMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public CarModelResponse mapCarModelToCarModelResponse(CarModel model){
        try {
            return CarModelResponse.builder()
                    .id(model.getId())
                    .name(model.getName())
                    .brandName(model.getBrand().getName())
                    .createdAt(ZonedDateTime.of(model.getCreatedAt(), ZoneId.of("UTC")))
                    .createdBy(model.getCreatedBy().toString())
                    .updatedAt(model.getUpdatedAt() != null ? ZonedDateTime.of(model.getUpdatedAt().toLocalDateTime(), ZoneId.of("UTC")): null)
                    .updatedBy(model.getUpdatedBy() != null ? model.getUpdatedBy().toString() : null)
                    .deletedAt(model.getDeletedAt() != null ? ZonedDateTime.of(model.getDeletedAt().toLocalDateTime(), ZoneId.of("UTC")) : null)
                    .deletedBy(model.getDeletedBy() != null ? model.getDeletedBy().toString() : null)
                    .build();
        }catch (Exception e){
            throw new RuntimeException("Error while mapping CarModel to CarModelResponse");
        }
    }

    public CarModelFilterResponse mapToCarModelFilterResponse(CarModel model, String brandName){
        try {
            return CarModelFilterResponse.builder()
                    .brandName(brandName)
                    .modelName(model.getName())
                    .build();
        }catch (Exception e){
            throw new RuntimeException("Error while mapping CarModel to CarModelFilterResponse");
        }
    }
}
