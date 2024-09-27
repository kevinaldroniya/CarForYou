package com.car.foryou.controller;

import com.car.foryou.dto.model.CarModelRequest;
import com.car.foryou.dto.model.CarModelResponse;
import com.car.foryou.service.CarModelService;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/models")
public class CarModelController {

    private final CarModelService carModelService;

    public CarModelController(CarModelService carModelService) {
        this.carModelService = carModelService;
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Page<CarModelResponse>> getAllModels(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortingDirection", defaultValue = "ASC") String sortingDirection,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy
    ){
        Page<CarModelResponse> response = carModelService.getAllModels(name, page, size, sortingDirection, sortBy);
        return ResponseEntity.ok(response);
    }

    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CarModelResponse> getModelById(@PathVariable("id") int id){
        CarModelResponse response = carModelService.getModelById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CarModelResponse> createModel(@RequestBody CarModelRequest carModelRequest){
        CarModelResponse response = carModelService.createModel(carModelRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping(
            path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CarModelResponse> updateModel(@PathVariable("id") int id, CarModelRequest carModelRequest){
        CarModelResponse response = carModelService.updateModel(id, carModelRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CarModelResponse> deleteModel(@PathVariable("id") int id){
        CarModelResponse response = carModelService.deleteModel(id);
        return ResponseEntity.ok(response);
    }
}
