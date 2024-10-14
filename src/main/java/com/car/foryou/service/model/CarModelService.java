package com.car.foryou.service.model;

import com.car.foryou.dto.model.CarModelFilterRequest;
import com.car.foryou.dto.model.CarModelRequest;
import com.car.foryou.dto.model.CarModelResponse;
import org.springframework.data.domain.Page;

public interface CarModelService {
    Page<CarModelResponse> getAllModels(CarModelFilterRequest filterRequest);
    CarModelResponse getModelById(int id);
    CarModelResponse createModel(CarModelRequest carModelRequest);
    CarModelResponse updateModel(int id, CarModelRequest carModelRequest);
    CarModelResponse deleteModel(int id);
    CarModelResponse getModelByBrandAndName(String brandName, String modelName);
}
