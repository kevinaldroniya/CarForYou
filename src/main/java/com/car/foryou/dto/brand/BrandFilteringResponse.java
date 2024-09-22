package com.car.foryou.dto.brand;

import com.car.foryou.model.Image;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BrandFilteringResponse {
    private String name;
    private Image image;
}
