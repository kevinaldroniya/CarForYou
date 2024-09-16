package com.car.foryou.service.impl;

import com.car.foryou.dto.brand.BrandRequest;
import com.car.foryou.dto.brand.BrandResponse;
import com.car.foryou.model.Brand;
import com.car.foryou.repository.BrandRepository;
import com.car.foryou.service.BrandService;
import com.car.foryou.util.mapper.BrandMapper;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    public BrandServiceImpl(BrandRepository brandRepository, BrandMapper brandMapper){
        this.brandRepository = brandRepository;
        this.brandMapper = brandMapper;
    }

    @Override
    public List<BrandResponse> getBrands() {
        List<Brand> brands = brandRepository.findAll();
        return brands.stream().map(brand -> {
            try {
                return brandMapper.mapBrandToBrandResponse(brand);
            } catch (Exception e) {
                throw new RuntimeException("Error while mapping Brand to BrandResponse");
            }
        }).toList();

    }

    @Override
    public BrandResponse createBrand(BrandRequest request) {
       try {
           brandRepository.findByName(request.getName()).ifPresent(brand -> {
               throw new RuntimeException("Brand with name " + request.getName() + " already exists");
           });
           Brand brand = brandMapper.mapBrandRequestToBrand(request);
           brand.setCreatedAt(ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond());
           brand.setCreatedBy(1L);
           Brand saved = brandRepository.save(brand);
           return brandMapper.mapBrandToBrandResponse(saved);
       }catch (Exception e) {
           throw new RuntimeException(e.getMessage());
       }
    }

    @Override
    public BrandResponse updateBrand(long id, BrandRequest request) {
        try {
            Brand brand = brandRepository.findById(id).orElseThrow(() -> new RuntimeException("Brand with id " + id + " not found"));
            brandRepository.findByName(request.getName()).ifPresent(b -> {
                if (b.getId() != id) {
                    throw new RuntimeException("Brand with name " + request.getName() + " already exists");
                }
            });
            Brand toBrand = brandMapper.mapBrandRequestToBrand(request);
            brand.setName(toBrand.getName());
            brand.setImage(toBrand.getImage());
            brand.setUpdatedAt(ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond());
            brand.setUpdatedBy(1L);
            Brand updated = brandRepository.save(brand);
            return brandMapper.mapBrandToBrandResponse(updated);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public BrandResponse deleteBrand(long id) {
        try {
            Brand brand = brandRepository.findById(id).orElseThrow(
                    () -> new RuntimeException("Brand with id " + id + " not found")
            );
            brand.setDeletedAt(ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond());
            brand.setDeletedBy(1L);
            Brand saved = brandRepository.save(brand);
            return brandMapper.mapBrandToBrandResponse(saved);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
