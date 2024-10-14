package com.car.foryou.service.filteringcar;

import org.springframework.data.domain.Page;

public interface FilteringCarService {
    Page<?> getFilteredCars(String brandName, String modelName, int year, int page, int size, String sortingDirection, String sortBy);
}
