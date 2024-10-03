package com.car.foryou.model;

import com.car.foryou.dto.item.Grade;
import com.car.foryou.dto.item.ItemStatus;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.ZonedDateTime;

@Entity
@Table(name = "item")
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Item extends BaseModel{

    @Column(name = "title")
    private String title;

    @Column(name = "license_plat")
    private String licensePlat;

    @Column(name = "brand")
    private String brand;

    @Column(name = "model")
    private String model;

    @Column(name = "variant")
    private String variant;

    @Column(name = "fuel_type")
    private String fuelType;

    @Column(name = "transmission")
    private String transmission;

    @Column(name = "engine_capacity")
    private String  engineCapacity;

    @Column(name = "year")
    private Integer year;

    @Column(name = "mileage")
    private Integer mileage;

    @Column(name = "starting_price")
    private String startingPrice;

    @Column(name = "physical_color")
    private String color;

    @Column(name = "auction_start_date")
    private ZonedDateTime auctionStart;

    @Column(name = "auction_end_date")
    private ZonedDateTime auctionEnd;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ItemStatus status;

    @Column(name = "interior_grade")
    @Enumerated(EnumType.STRING)
    private Grade interiorGrade;

    @Column(name = "exterior_grade")
    @Enumerated(EnumType.STRING)
    private Grade exteriorGrade;

    @Column(name = "chassis_grade")
    @Enumerated(EnumType.STRING)
    private Grade chassisGrade;

    @Column(name = "engine_grade")
    @Enumerated(EnumType.STRING)
    private Grade engineGrade;
}
