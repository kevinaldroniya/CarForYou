package com.car.foryou.dto.variant;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class VariantResponse {
    private long id;
    private String model;
    private String name;
    private int year;
    private Set<String> engine;
    private Set<String> transmission;
    private Set<String> fuel;
}
