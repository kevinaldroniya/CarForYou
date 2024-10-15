package com.car.foryou.service.model;

import com.car.foryou.dto.brand.BrandResponse;
import com.car.foryou.dto.model.CarModelFilterRequest;
import com.car.foryou.dto.model.CarModelRequest;
import com.car.foryou.dto.model.CarModelResponse;
import com.car.foryou.exception.ConversionException;
import com.car.foryou.exception.GeneralException;
import com.car.foryou.exception.ResourceAlreadyExistsException;
import com.car.foryou.exception.ResourceNotFoundException;
import com.car.foryou.mapper.BrandMapper;
import com.car.foryou.model.Brand;
import com.car.foryou.model.CarModel;
import com.car.foryou.repository.model.ModelRepository;
import com.car.foryou.service.brand.BrandService;
import com.car.foryou.mapper.CarModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;


@Service
public class CarModelServiceImpl implements CarModelService {

    private final ModelRepository modelRepository;
    private final CarModelMapper carModelMapper;
    private final BrandService brandService;
    private static final String MODEL = "Model";
    private static final String ID = "ID";

    public CarModelServiceImpl(ModelRepository modelRepository, CarModelMapper carModelMapper, BrandService brandService) {
        this.modelRepository = modelRepository;
        this.carModelMapper = carModelMapper;
        this.brandService = brandService;
    }

    @Override
    public CarModelResponse getModelById(int id) {
       try {
           CarModel carModel = findCarModelById(id);
           return carModelMapper.mapCarModelToCarModelResponse(carModel);
       }catch (ConversionException e){
           throw new GeneralException(e.getMessage(), e.getStatus());
       }
    }

    @Override
    public Page<CarModelResponse> getAllModels(CarModelFilterRequest filterRequest) {
        try {
            Sort sort = filterRequest.getSortDirection().equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(filterRequest.getSortBy()).ascending() : Sort.by(filterRequest.getSortBy()).descending();
            Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize(), sort);
            Page<CarModel> models = modelRepository.findByNameContaining(filterRequest.getName(), pageable);
            return models.map(carModelMapper::mapCarModelToCarModelResponse);
        }catch (ConversionException e){
            throw new GeneralException(e.getMessage(), e.getStatus());
        }
    }

    @Override
    public CarModelResponse createModel(CarModelRequest carModelRequest) {
        try {
            Brand brand = getBrand(carModelRequest.getBrandName());
            modelRepository.findByName(carModelRequest.getName()).ifPresent(model -> {
                throw new ResourceAlreadyExistsException(MODEL, HttpStatus.CONFLICT);
            });
            CarModel carModel = CarModel.builder()
                    .name(carModelRequest.getName())
                    .brand(brand)
                    .build();
            CarModel save = modelRepository.save(carModel);
            return carModelMapper.mapCarModelToCarModelResponse(save);
        }catch (ConversionException e) {
            throw new GeneralException(e.getMessage(), e.getStatus());
        }

    }

    @Override
    public CarModelResponse updateModel(int id, CarModelRequest carModelRequest) {
        try {
            CarModel carModel = findCarModelById(id);
            Brand brand = getBrand(carModelRequest.getBrandName());
            modelRepository.findByName(carModelRequest.getName()).ifPresent(model -> {
                if (model.getId() != id) {
                    throw new ResourceAlreadyExistsException(MODEL, HttpStatus.CONFLICT);
                }
            });
            carModel.setName(carModelRequest.getName());
            carModel.setBrand(brand);
            CarModel updated = modelRepository.save(carModel);
            return carModelMapper.mapCarModelToCarModelResponse(updated);
        }catch (ConversionException e) {
            throw new GeneralException(e.getMessage(), e.getStatus());
        }
    }

    @Override
    public CarModelResponse deleteModel(int id) {
        try {
            CarModel carModel =findCarModelById(id);
            carModel.setDeletedAt(Instant.now());
            CarModel deleted = modelRepository.save(carModel);
            return carModelMapper.mapCarModelToCarModelResponse(deleted);
        }catch (ConversionException e) {
            throw new GeneralException(e.getMessage(), e.getStatus());
        }
    }

    @Override
    public CarModelResponse getModelByBrandAndName(String brandName, String modelName) {
        CarModel carModel = modelRepository.findByNameAndBrandName(modelName, brandName).orElseThrow(
                () -> new ResourceNotFoundException(MODEL, "name", modelName)
        );
        return carModelMapper.mapCarModelToCarModelResponse(carModel);
    }


    private Brand getBrand(String brandName){
        BrandResponse brandByName = brandService.getBrandByName(brandName);
        return BrandMapper.mapBrandResponseToBrand(brandByName);
    }

    private CarModel findCarModelById(int id){
        return modelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(MODEL, ID, id));
    }
}
