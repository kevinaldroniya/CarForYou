package com.car.foryou.service.impl;

import com.car.foryou.dto.model.CarModelRequest;
import com.car.foryou.dto.model.CarModelResponse;
import com.car.foryou.model.Brand;
import com.car.foryou.model.CarModel;
import com.car.foryou.repository.BrandRepository;
import com.car.foryou.repository.ModelRepository;
import com.car.foryou.service.CarModelService;
import com.car.foryou.mapper.CarModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class CarModelServiceImpl implements CarModelService {

    private final ModelRepository modelRepository;
    private final BrandRepository brandRepository;
    private final CarModelMapper carModelMapper;

    public CarModelServiceImpl(ModelRepository modelRepository, BrandRepository brandRepository, CarModelMapper carModelMapper) {
        this.modelRepository = modelRepository;
        this.brandRepository = brandRepository;
        this.carModelMapper = carModelMapper;
    }


    @Override
    public CarModelResponse getModelById(int id) {
        return null;
    }

    @Override
    public Page<CarModelResponse> getAllModels(String name, int page, int size, String sortingDirection, String sortBy) {
        Sort sort = sortingDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CarModel> models = null;
        if (name == null){
            models = modelRepository.findAll(pageable);
        }else{
            models = modelRepository.findByNameContaining(name, pageable);
        }
        return models.map(carModelMapper::mapCarModelToCarModelResponse);
    }

    @Override
    public CarModelResponse createModel(CarModelRequest carModelRequest) {
        try {
            Brand brand = brandRepository.findByName(carModelRequest.getBrandName()).orElseThrow(
                    () -> new RuntimeException("Brand with name " + carModelRequest.getBrandName() + " not found"));
            modelRepository.findByName(carModelRequest.getName()).ifPresent(model -> {
                throw new RuntimeException("Model with name " + carModelRequest.getName() + " already exists");
            });
            CarModel carModel = CarModel.builder()
                    .name(carModelRequest.getName())
                    .brand(brand)
                    .createdAt((int) ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond())
                    .createdBy(1)
                    .build();
            CarModel save = modelRepository.save(carModel);
            return carModelMapper.mapCarModelToCarModelResponse(save);
        }catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public CarModelResponse updateModel(int id, CarModelRequest carModelRequest) {
        try {
            CarModel carModel = modelRepository.findById(id).orElseThrow(() -> new RuntimeException("Model with id " + id + " not found"));
            Brand brand = brandRepository.findByName(carModelRequest.getBrandName()).orElseThrow(
                    () -> new RuntimeException("Brand with name " + carModelRequest.getBrandName() + " not found"));
            modelRepository.findByName(carModelRequest.getName()).ifPresent(model -> {
                if (model.getId() != id) {
                    throw new RuntimeException("Model with name " + carModelRequest.getName() + " already exists");
                }
            });
            carModel.setName(carModelRequest.getName());
            carModel.setBrand(brand);
            carModel.setUpdatedAt((int) ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond());
            carModel.setUpdatedBy(1);
            CarModel updated = modelRepository.save(carModel);
            return carModelMapper.mapCarModelToCarModelResponse(updated);
        }catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public CarModelResponse deleteModel(int id) {
        try {
            CarModel carModel = modelRepository.findById(id).orElseThrow(() -> new RuntimeException("Model with id " + id + " not found"));
            carModel.setDeletedAt((int) ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond());
            carModel.setDeletedBy(1);
            CarModel deleted = modelRepository.save(carModel);
            return carModelMapper.mapCarModelToCarModelResponse(deleted);
        }catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
