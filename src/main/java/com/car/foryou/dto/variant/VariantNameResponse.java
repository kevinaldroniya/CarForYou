package com.car.foryou.dto.variant;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VariantNameResponse {
    private String brandName;
    private String modelName;
    private Integer releaseYear;
    private String variant;
    private Set<String> engine;
    private Set<String> transmission;
    private Set<String> fuel;
}
