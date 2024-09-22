package com.car.foryou.dto.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CarModelFilterResponse {
    private String brandName;
    private String modelName;
}
