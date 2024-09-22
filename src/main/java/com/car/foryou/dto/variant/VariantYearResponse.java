package com.car.foryou.dto.variant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VariantYearResponse {
    private String brandName;
    private String modelName;
    private int year;
}
