package com.car.foryou.dto.model;

import com.car.foryou.dto.FilterParam;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class CarModelFilterRequest extends FilterParam {
    private String name;
}
