package com.car.foryou.util.mapper;

import com.car.foryou.dto.brand.BrandFilteringResponse;
import com.car.foryou.dto.brand.BrandRequest;
import com.car.foryou.dto.brand.BrandResponse;
import com.car.foryou.model.Brand;
import com.car.foryou.model.Image;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;

@Component
public class BrandMapper {

    private final ObjectMapper objectMapper;

    public BrandMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public BrandResponse mapBrandToBrandResponse(Brand brand){
       try {
           Instant createdAt = Instant.ofEpochSecond(brand.getCreatedAt());
           Instant updatedAt = brand.getUpdatedAt() != null ? Instant.ofEpochSecond(brand.getUpdatedAt()) : null;
           Instant deletedAt = brand.getDeletedAt() != null ? Instant.ofEpochSecond(brand.getDeletedAt()) : null;
           return BrandResponse.builder()
                   .id(brand.getId())
                   .name(brand.getName())
                   .image(objectMapper.readValue(brand.getImage(), Image.class))
                   .createdAt(createdAt.atZone(ZoneId.of("UTC")))
                   .createdBy(brand.getCreatedBy().toString())
                   .updatedAt(updatedAt != null ? updatedAt.atZone(ZoneId.of("UTC")) : null)
                   .updatedBy(brand.getUpdatedBy() != null ? brand.getUpdatedBy().toString() : null)
                   .deletedAt(deletedAt != null ? deletedAt.atZone(ZoneId.of("UTC")) : null)
                   .deletedBy(brand.getDeletedBy() != null ? brand.getDeletedBy().toString() : null)
                   .build();
       }catch (Exception e){
           throw new RuntimeException("Error while mapping Brand to BrandResponse");
       }
    }

    public Brand mapBrandRequestToBrand(BrandRequest request){
       try {
           return Brand.builder()
                   .name(request.getName())
                   .image(objectMapper.writeValueAsString(request.getImage()))
                   .build();
       }catch (Exception e){
           throw new RuntimeException("Error while mapping BrandRequest to Brand");
       }
    }

    public BrandFilteringResponse mapToBrandFilterResponse(Brand brand){
        try {
            return BrandFilteringResponse.builder()
                    .name(brand.getName())
                    .image(objectMapper.readValue(brand.getImage(), Image.class))
                    .build();
        }catch (Exception e){
            throw new RuntimeException("Error while mapping Brand to BrandFilterResponse");
        }
    }
}
