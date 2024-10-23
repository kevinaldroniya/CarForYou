package com.car.foryou.dto.item;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemRequest {
    private String title;
    private String licensePlate;
    private String brand;
    private String model;
    private String variant;
    private int year;
    private String fuelType;
    private String transmission;
    private String engineCapacity;
    private int mileage;
    private Long startingPrice;
    private String color;
    private ItemGrade interiorItemGrade;
    private ItemGrade exteriorItemGrade;
    private ItemGrade chassisItemGrade;
    private ItemGrade engineItemGrade;
}
