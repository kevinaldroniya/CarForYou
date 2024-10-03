package com.car.foryou.service;

import com.car.foryou.dto.brand.BrandRequest;
import com.car.foryou.dto.brand.BrandResponse;

import java.util.List;

public interface BrandService {
    List<BrandResponse> getBrands();
    BrandResponse getBrandByName(String name);
    BrandResponse createBrand(BrandRequest request);
    BrandResponse updateBrand(int id, BrandRequest request);
    BrandResponse deleteBrand(int id);
}
