package com.car.foryou.dto.brand;

import com.car.foryou.model.Image;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class BrandRequest {
    private String name;
    private Image image;
}
