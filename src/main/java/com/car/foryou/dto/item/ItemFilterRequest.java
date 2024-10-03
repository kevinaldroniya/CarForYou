package com.car.foryou.dto.item;

import com.car.foryou.dto.FilterParam;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ItemFilterRequest extends FilterParam {
    private String search = "";
    private String brand = "";
    private String model = "";
    private String variant = "";
    private Integer year = 0;
}
