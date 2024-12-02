package com.car.foryou.dto;

import jakarta.validation.constraints.Pattern;

import lombok.Data;

@Data
public abstract class FilterParam {
    private Integer page = 0;
    private Integer size = 10;
    private String sortBy = "id";
    @Pattern(regexp = "ASC|DESC", message = "sortDirection must be 'ASC' or 'DESC'")
    private String sortDirection = "ASC";

    public Integer getPage() {
        return (page == null ? 0 : page);
    }

    public String getSortBy() {
        return (sortBy == null || sortBy.isEmpty() ? "id" : sortBy);
    }

    public String getSortDirection() {
        return (sortDirection == null || sortDirection.isEmpty() ? "ASC" : sortDirection);
    }

    public Integer getSize() {
        return (size == null ? 10 : size);
    }
}
