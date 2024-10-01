package com.car.foryou.dto.variant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VariantCriteria {
    private String name;
    private String model;
    private int year;
    private String fuelType;
    private String transmission;
    private String engine;
}
