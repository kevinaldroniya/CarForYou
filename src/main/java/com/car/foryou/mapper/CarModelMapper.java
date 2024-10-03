package com.car.foryou.mapper;

import com.car.foryou.dto.model.CarModelFilterResponse;
import com.car.foryou.dto.model.CarModelResponse;
import com.car.foryou.model.CarModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class CarModelMapper {

    public CarModelResponse mapCarModelToCarModelResponse(CarModel model){
        try {
            return CarModelResponse.builder()
                    .id(model.getId())
                    .name(model.getName())
                    .brandName(model.getBrand().getName())
                    .createdAt(ZonedDateTime.ofInstant(model.getCreatedAt(), ZoneId.of("UTC")))
                    .createdBy(model.getCreatedBy().toString())
                    .updatedAt(model.getUpdatedAt() != null ? ZonedDateTime.ofInstant(model.getUpdatedAt(), ZoneId.of("UTC")): null)
                    .updatedBy(model.getUpdatedBy() != null ? model.getUpdatedBy().toString() : null)
                    .deletedAt(model.getDeletedAt() != null ? ZonedDateTime.ofInstant(model.getDeletedAt(), ZoneId.of("UTC")) : null)
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
