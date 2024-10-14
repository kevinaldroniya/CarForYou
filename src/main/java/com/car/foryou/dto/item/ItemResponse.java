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
    private String engineCapacity;
    private int mileage;
    private String startingPrice;
    private String color;
    private ItemStatus status;
    private ZonedDateTime auctionStart;
    private ZonedDateTime auctionEnd;
    private ItemGrade interiorItemGrade;
    private ItemGrade exteriorItemGrade;
    private ItemGrade chassingItemGrade;
    private ItemGrade engineItemGrade;
    private ZonedDateTime createdAt;
}
