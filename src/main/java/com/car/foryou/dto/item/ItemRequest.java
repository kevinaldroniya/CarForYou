package com.car.foryou.dto.item;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ItemRequest {
    private String title;
    private String licensePlate;
    private String brand;
    private String model;
    private String variant;
    private String fuelType;
    private String transmission;
    private int engineCapacity;
    private int mileage;
    private String startingPrice;
    private String color;
    private Grade interiorGrade;
    private Grade exteriorGrade;
    private Grade chassingGrade;
    private Grade engineGrade;
}
