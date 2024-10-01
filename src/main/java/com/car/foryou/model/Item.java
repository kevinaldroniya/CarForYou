package com.car.foryou.model;

import com.car.foryou.dto.item.Grade;
import com.car.foryou.dto.item.ItemStatus;
import com.car.foryou.service.impl.CustomUserDetailService;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.ZonedDateTime;

@Entity
@Table(name = "item")
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Item {

    @Column(name = "title")
    private String title;

    @Column(name = "license_plat")
    private String licensePlat;

    @ManyToOne
    @JoinColumn(name = "inspector_id")
    private User inspector;

    @ManyToOne
    @JoinColumn(name = "variant_id")
    private Variant variant;

    @Column(name = "fuel_type")
    private String fuelType;

    @Column(name = "transmission")
    private String transmission;

    @Column(name = "engine_capacity")
    private String  engineCapacity;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @CreatedDate
    @Column(name = "created_at")
    private Instant createdAt;

    @CreatedBy
    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "deleted_by")
    private Integer deletedBy;

    @PreUpdate
    public void onUpdate(){
        if (this.deletedAt != null){
            this.deletedAt= Instant.now();
            this.deletedBy= CustomUserDetailService.getLoggedInUserDetails().getId();
        }else{
            this.updatedAt=Instant.now();
            this.updatedBy=CustomUserDetailService.getLoggedInUserDetails().getId();
        }
    }
}
