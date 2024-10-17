package com.car.foryou.mapper;

import com.car.foryou.dto.brand.BrandFilteringResponse;
import com.car.foryou.dto.brand.BrandRequest;
import com.car.foryou.dto.brand.BrandResponse;
import com.car.foryou.exception.ConversionException;
import com.car.foryou.model.Brand;
import com.car.foryou.dto.Image;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class BrandMapper {

    private final ObjectMapper objectMapper;
    private static final String BRAND = "BRAND";
    private static final String UTC = "UTC";

    public BrandMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public BrandResponse mapBrandToBrandResponse(Brand brand){
       try {
           return BrandResponse.builder()
                   .id(brand.getId())
                   .name(brand.getName())
                   .image(objectMapper.readValue(brand.getImage(), Image.class))
                   .createdAt(brand.getCreatedAt().atZone(ZoneId.of(UTC)))
                   .createdBy(brand.getCreatedBy().toString())
                   .updatedAt(brand.getUpdatedAt() != null ? ZonedDateTime.ofInstant(brand.getUpdatedAt(), ZoneId.of(UTC)): null)
                   .updatedBy(brand.getUpdatedBy() != null ? brand.getUpdatedBy().toString() : null)
                   .deletedAt(brand.getDeletedAt() != null ? ZonedDateTime.ofInstant(brand.getDeletedAt(), ZoneId.of(UTC)) : null)
                   .deletedBy(brand.getDeletedBy() != null ? brand.getDeletedBy().toString() : null)
                   .build();
       }catch (JsonProcessingException e){
           throw new ConversionException(BRAND, "BrandResponse", HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    public Brand mapBrandRequestToBrand(BrandRequest request){
       try {
           return Brand.builder()
                   .name(request.getName())
                   .image(objectMapper.writeValueAsString(request.getImage()))
                   .build();
       }catch (Exception e){
           throw new ConversionException("BrandRequest", BRAND, HttpStatus.BAD_REQUEST);
       }
    }

    public BrandFilteringResponse mapToBrandFilterResponse(Brand brand){
        try {
            return BrandFilteringResponse.builder()
                    .name(brand.getName())
                    .image(objectMapper.readValue(brand.getImage(), Image.class))
                    .build();
        }catch (Exception e){
            throw new ConversionException(BRAND, "BrandFilterResponse", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static Brand mapBrandResponseToBrand(BrandResponse brandResponse){
        return Brand.builder()
                .id(brandResponse.getId())
                .name(brandResponse.getName())
                .build();
    }
}
