package com.car.foryou.dto;

import jakarta.validation.constraints.Pattern;

import lombok.Data;

@Data
public abstract class FilterParam {
    private int page = 0;
    private int size = 10;
    private String sortBy = "id";
    @Pattern(regexp = "ASC|DESC", message = "sortDirection must be 'ASC' or 'DESC'")
    private String sortDirection = "ASC";
}
