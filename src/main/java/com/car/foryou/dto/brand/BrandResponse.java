package com.car.foryou.dto.brand;

import com.car.foryou.model.Image;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Data
@Builder
@Getter
@Setter
public class BrandResponse {
    private int id;
    private String name;
    private Image image;
    private ZonedDateTime createdAt;
    private String createdBy;
    private ZonedDateTime updatedAt;
    private String updatedBy;
    private ZonedDateTime deletedAt;
    private String deletedBy;
}
