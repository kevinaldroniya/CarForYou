package com.car.foryou.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Image {
    private String imageId;
    private String large;
    private String medium;
    private String small;
}
