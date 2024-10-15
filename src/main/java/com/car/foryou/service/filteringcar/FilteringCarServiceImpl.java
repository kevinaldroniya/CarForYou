package com.car.foryou.service.filteringcar;

import com.car.foryou.dto.variant.VariantYearResponse;
import com.car.foryou.model.Brand;
import com.car.foryou.model.CarModel;
import com.car.foryou.model.Variant;
import com.car.foryou.repository.brand.BrandRepository;
import com.car.foryou.repository.model.ModelRepository;
import com.car.foryou.repository.variant.VariantRepository;
import com.car.foryou.mapper.BrandMapper;
import com.car.foryou.mapper.CarModelMapper;
import com.car.foryou.mapper.VariantMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class FilteringCarServiceImpl implements FilteringCarService {

    private final BrandRepository brandRepository;
    private final ModelRepository modelRepository;
    private final VariantRepository variantRepository;
    private final BrandMapper brandMapper;
    private final CarModelMapper carModelMapper;
    private final VariantMapper variantMapper;


    public FilteringCarServiceImpl(BrandRepository brandRepository, ModelRepository modelRepository, VariantRepository variantRepository, BrandMapper brandMapper, CarModelMapper carModelMapper, VariantMapper variantMapper) {
        this.brandRepository = brandRepository;
        this.modelRepository = modelRepository;
        this.variantRepository = variantRepository;
        this.brandMapper = brandMapper;
        this.carModelMapper = carModelMapper;
        this.variantMapper = variantMapper;
    }

    @Override
    public Page<?> getFilteredCars(String brandName, String modelName, int year, int page, int size, String sortingDirection, String sortBy) {
        Sort sort = sortingDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        if (brandName == null || brandName.isEmpty()) {
            Page<Brand> brandPage = brandRepository.findAll(pageable);
            return brandPage.map(brandMapper::mapToBrandFilterResponse);
        }

        Brand brand = brandRepository.findByName(brandName).orElseThrow(
                () -> new RuntimeException("Brand Not Found")
        );

        if (modelName == null || modelName.isEmpty()) {
            Page<CarModel> carModels = modelRepository.findAllByBrandId(brand.getId(), pageable);
            return carModels.map(model -> carModelMapper.mapToCarModelFilterResponse(model, brandName));
        }

        CarModel carModel = modelRepository.findByName(modelName).orElseThrow(
                () -> new RuntimeException("Model Not Found")
        );

        Page<Variant> variants;
        if (year != 0) {
            variants = variantRepository.findAllByCarModelIdAndYear(carModel.getId(), year, pageable);
            return variants.map(variant -> variantMapper.mapVariantToVariantNameResponse(variant, brandName, modelName));
        } else {
            sortBy="year";
            sort = sortingDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            pageable = PageRequest.of(page, size, sort);
            Page<Long> all = variantRepository.findAllByCarModelId(carModel.getId(), pageable);
            Page<VariantYearResponse> map = all.map(aLong -> variantMapper.mapVariantToYearResponse(aLong, brandName, modelName));
            return map;
        }

    }
}
