package com.car.foryou.dto.variant;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class VariantRequest {
    private String name;
    private int year;
    private Set<String> engine;
    private Set<String> transmission;
    private Set<String> fuel;
    private String model;
}
