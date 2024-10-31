package com.car.foryou.service.model;

import com.car.foryou.dto.model.CarModelFilterRequest;
import com.car.foryou.dto.model.CarModelRequest;
import com.car.foryou.dto.model.CarModelResponse;
import com.car.foryou.model.CarModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CarModelService {
    List<CarModel> getAllCarModels();
    CarModel getCarModelById(Integer id);
    CarModel getCarModelByName(String brandName);
    Page<CarModelResponse> getAllModelsResponse(CarModelFilterRequest filterRequest);
    CarModelResponse getModelResponseById(int id);
    CarModelResponse createModel(CarModelRequest carModelRequest);
    CarModelResponse updateModel(int id, CarModelRequest carModelRequest);
    CarModelResponse deleteModel(int id);
    CarModelResponse getModelResponseByBrandAndName(String brandName, String modelName);
}
