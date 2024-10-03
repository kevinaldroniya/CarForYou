package com.car.foryou.service;

import com.car.foryou.dto.model.CarModelRequest;
import com.car.foryou.dto.model.CarModelResponse;
import org.springframework.data.domain.Page;

public interface CarModelService {
    Page<CarModelResponse> getAllModels(String name, int page, int size, String sortingDirection, String sortBy);
    CarModelResponse getModelById(int id);
    CarModelResponse createModel(CarModelRequest carModelRequest);
    CarModelResponse updateModel(int id, CarModelRequest carModelRequest);
    CarModelResponse deleteModel(int id);
    CarModelResponse getModelByBrandAndName(String brandName, String modelName);
}
