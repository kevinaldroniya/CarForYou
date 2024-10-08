package com.car.foryou.service;

import com.car.foryou.dto.brand.BrandFilterRequest;
import com.car.foryou.dto.brand.BrandRequest;
import com.car.foryou.dto.brand.BrandResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BrandService {
    Page<BrandResponse> getPaginatedBrands(BrandFilterRequest filterRequest);
    List<BrandResponse> getBrands();
    BrandResponse getBrandByName(String name);
    BrandResponse createBrand(BrandRequest request);
    BrandResponse updateBrand(int id, BrandRequest request);
    BrandResponse deleteBrand(int id);
    BrandResponse getBrandById(int id);
}
