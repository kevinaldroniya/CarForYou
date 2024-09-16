package com.car.foryou.util.mapper;

import com.car.foryou.dto.variant.VariantRequest;
import com.car.foryou.dto.variant.VariantResponse;
import com.car.foryou.model.CarModel;
import com.car.foryou.model.Variant;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class VariantMapper {

    private final ObjectMapper objectMapper;

    public VariantMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Variant mapVariantRequestToVariant(VariantRequest request, CarModel model){
        try {
            return Variant.builder()
                    .name(request.getName())
                    .year(request.getYear())
                    .engine(objectMapper.writeValueAsString(request.getEngine()))
                    .transmission(objectMapper.writeValueAsString(request.getTransmission()))
                    .fuel(objectMapper.writeValueAsString(request.getFuel()))
                    .carModel(model)
                    .build();
        }catch (Exception e){
            throw new RuntimeException("Error while mapping VariantRequest to Variant");
        }
    }

    public VariantResponse mapVariantToVariantResponse(Variant variant){
        try {
            return VariantResponse.builder()
                    .id(variant.getId())
                    .name(variant.getName())
                    .year(variant.getYear())
                    .engine(objectMapper.readValue(variant.getEngine(), Set.class))
                    .transmission(objectMapper.readValue(variant.getTransmission(), Set.class))
                    .fuel(objectMapper.readValue(variant.getFuel(), Set.class))
                    .model(variant.getCarModel().getName())
                    .build();
        }catch (Exception e){
            throw new RuntimeException("Error while mapping Variant to VariantResponse");
        }
    }
}
