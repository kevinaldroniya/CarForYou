package com.car.foryou.dto.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CarModelRequest {
    String name;
    String brandName;
}
