package com.car.foryou.service.impl;

import com.car.foryou.dto.brand.BrandRequest;
import com.car.foryou.dto.brand.BrandResponse;
import com.car.foryou.exception.ResourceNotFoundException;
import com.car.foryou.model.Brand;
import com.car.foryou.repository.BrandRepository;
import com.car.foryou.service.BrandService;
import com.car.foryou.mapper.BrandMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class BrandServiceImpl implements BrandService  {

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
    public BrandResponse getBrandByName(String name) {
        Brand brand = brandRepository.findByName(name).orElseThrow(
                () -> new ResourceNotFoundException("Brand","Name",name));
        return brandMapper.mapBrandToBrandResponse(brand);
    }

    @Override
    public BrandResponse createBrand(BrandRequest request) {
       try {
           brandRepository.findByName(request.getName()).ifPresent(brand -> {
               throw new ResourceNotFoundException("Brand","ID",request.getName());
           });
           Brand brand = brandMapper.mapBrandRequestToBrand(request);
           Brand saved = brandRepository.save(brand);
           return brandMapper.mapBrandToBrandResponse(saved);
       }catch (Exception e) {
           throw new RuntimeException(e.getMessage());
       }
    }

    @Override
    public BrandResponse updateBrand(int id, BrandRequest request) {
        try {
            Brand brand = brandRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Brand","ID",request.getName()));
            brandRepository.findByName(request.getName()).ifPresent(b -> {
                if (b.getId() != id) {
                    throw new RuntimeException("Brand with name " + request.getName() + " already exists");
                }
            });
            Brand toBrand = brandMapper.mapBrandRequestToBrand(request);
            brand.setName(toBrand.getName());
            brand.setImage(toBrand.getImage());
            Brand updated = brandRepository.save(brand);
            return brandMapper.mapBrandToBrandResponse(updated);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public BrandResponse deleteBrand(int id) {
        try {
            Brand brand = brandRepository.findById(id).orElseThrow(
                    () -> new RuntimeException("Brand with id " + id + " not found")
            );
            brand.setDeletedAt(Instant.now());
            Brand saved = brandRepository.save(brand);
            return brandMapper.mapBrandToBrandResponse(saved);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
