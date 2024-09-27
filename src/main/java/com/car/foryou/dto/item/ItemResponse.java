package com.car.foryou.dto.item;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class ItemResponse {
    private int itemId;
    private String title;
    private String licensePlate;
    private String inspector;
    private String brand;
    private String model;
    private String variant;
    private String fuelType;
    private String transmission;
    private int year;
    private int engineCapacity;
    private int mileage;
    private String startingPrice;
    private String color;
    private ItemStatus status;
    private ZonedDateTime auctionStart;
    private ZonedDateTime auctionEnd;
    private Grade interiorGrade;
    private Grade exteriorGrade;
    private Grade chassingGrade;
    private Grade engineGrade;
    private ZonedDateTime createdAt;
}
