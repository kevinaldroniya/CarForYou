package com.car.foryou.dto.variant;

import com.car.foryou.model.CarModel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VariantCriteriaRequest {
    private String name;
    private String model;
    private int year;
    private String fuelType;
    private String transmission;
    private String engine;
}
