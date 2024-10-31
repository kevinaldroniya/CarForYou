package com.car.foryou.controller;

import com.car.foryou.api.v1.BaseApiControllerV1;
import com.car.foryou.dto.model.CarModelFilterRequest;
import com.car.foryou.dto.model.CarModelRequest;
import com.car.foryou.dto.model.CarModelResponse;
import com.car.foryou.service.model.CarModelService;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/models")
public class CarModelController implements BaseApiControllerV1 {

    private final CarModelService carModelService;

    public CarModelController(CarModelService carModelService) {
        this.carModelService = carModelService;
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Page<CarModelResponse>> getAllModels(@ModelAttribute CarModelFilterRequest filterRequest){
        Page<CarModelResponse> response = carModelService.getAllModelsResponse(filterRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CarModelResponse> getModelById(@PathVariable("id") int id){
        CarModelResponse response = carModelService.getModelResponseById(id);
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

    @PostMapping(
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

    @GetMapping(
            path = "/{brandName}/{modelName}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CarModelResponse> getModelByBrandAndName(@PathVariable("brandName") String brandName, @PathVariable("modelName") String modelName){
        CarModelResponse response = carModelService.getModelResponseByBrandAndName(brandName, modelName);
        return ResponseEntity.ok(response);
    }
}
