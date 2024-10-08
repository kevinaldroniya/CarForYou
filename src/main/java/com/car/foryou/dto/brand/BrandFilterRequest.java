package com.car.foryou.dto.brand;

import com.car.foryou.dto.FilterParam;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class BrandFilterRequest extends FilterParam {
    private String name = "";
}
