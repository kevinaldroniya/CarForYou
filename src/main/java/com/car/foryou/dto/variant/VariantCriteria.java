package com.car.foryou.dto.variant;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VariantCriteria {
    @NotNull(message = "name cannot be null")
    private String name;
    @NotNull(message = "model cannot be null")
    private String model;
    @NotNull(message = "year cannot be null")
    @Min(value = 1900, message = "year must be greater than 1900")
    private Integer year;
    @NotNull(message = "fuelType cannot be null")
    private String fuelType;
    @NotNull(message = "transmission cannot be null")
    private String transmission;
    @NotNull(message = "engine cannot be null")
    private String engine;
}
