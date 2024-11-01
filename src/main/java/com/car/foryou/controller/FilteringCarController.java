package com.car.foryou.controller;

import com.car.foryou.service.filteringcar.FilteringCarService;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/filtering")
public class FilteringCarController extends BaseApiControllerV1 {

    public final FilteringCarService filteringCarService;

    public FilteringCarController(FilteringCarService filteringCarService) {
        this.filteringCarService = filteringCarService;
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Page<?>> getAll(
            @RequestParam(value = "brandName", defaultValue = "", required = false)String brandName,
            @RequestParam(value = "modelName", defaultValue = "", required = false)String modelName,
            @RequestParam(value = "year", defaultValue = "0", required = false) int year,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortingDirection", defaultValue = "ASC") String sortingDirection,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy
    ){
        Page<?> filteredCars = filteringCarService.getFilteredCars(brandName, modelName, year, page, size, sortingDirection, sortBy);
        return ResponseEntity.ok(filteredCars);
    }


}
