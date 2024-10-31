package com.car.foryou.service.brand;

import com.car.foryou.dto.brand.BrandFilterRequest;
import com.car.foryou.dto.brand.BrandRequest;
import com.car.foryou.dto.brand.BrandResponse;
import com.car.foryou.model.Brand;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BrandService {
    List<Brand> getAllBrands();
    Brand getBrandById(Integer id);
    Brand getBrandByName(String brandName);
    Page<BrandResponse> getPaginatedBrands(BrandFilterRequest filterRequest);
    List<BrandResponse> getBrandsResponse();
    BrandResponse getBrandResponseByName(String name);
    BrandResponse createBrand(BrandRequest request);
    BrandResponse updateBrand(int id, BrandRequest request);
    BrandResponse deleteBrand(int id);
    BrandResponse getBrandResponseById(int id);
}
