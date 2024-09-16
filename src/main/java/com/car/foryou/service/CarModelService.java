package com.car.foryou.service;

import com.car.foryou.dto.model.CarModelRequest;
import com.car.foryou.dto.model.CarModelResponse;
import org.springframework.data.domain.Page;

public interface CarModelService {
    Page<CarModelResponse> getAllModels(String name, int page, int size, String sortingDirection, String sortBy);
    CarModelResponse getModelById(long id);
    CarModelResponse createModel(CarModelRequest carModelRequest);
    CarModelResponse updateModel(long id, CarModelRequest carModelRequest);
    CarModelResponse deleteModel(long id);
}
