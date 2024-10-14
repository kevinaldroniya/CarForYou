package com.car.foryou.service.brand;

import com.car.foryou.dto.brand.BrandFilterRequest;
import com.car.foryou.dto.brand.BrandRequest;
import com.car.foryou.dto.brand.BrandResponse;
import com.car.foryou.exception.ConversionException;
import com.car.foryou.exception.GeneralException;
import com.car.foryou.exception.ResourceAlreadyExistsException;
import com.car.foryou.exception.ResourceNotFoundException;
import com.car.foryou.model.Brand;
import com.car.foryou.repository.BrandRepository;
import com.car.foryou.mapper.BrandMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class BrandServiceImpl implements BrandService  {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;
    private static final String BRAND = "BRAND";
    private static final String ID = "ID";

    public BrandServiceImpl(BrandRepository brandRepository, BrandMapper brandMapper){
        this.brandRepository = brandRepository;
        this.brandMapper = brandMapper;
    }


    @Override
    public Page<BrandResponse> getPaginatedBrands(BrandFilterRequest filterRequest) {
       try {
           Sort sort = filterRequest.getSortDirection().equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(filterRequest.getSortBy()).ascending() : Sort.by(filterRequest.getSortBy()).descending();
           Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize(), sort);
           Page<Brand> brandPage = brandRepository.findAll(filterRequest.getName(), pageable);
           return brandPage.map(brandMapper::mapBrandToBrandResponse);
       }catch (ConversionException e){
           throw new GeneralException(e.getMessage(), e.getStatus());
       }
    }

    @Override
    public List<BrandResponse> getBrands() {
        log.info("Fetching all brands");
        List<Brand> brands = brandRepository.findAll();
        return brands.stream().map(brand -> {
            try {
                return brandMapper.mapBrandToBrandResponse(brand);
            } catch (ConversionException e) {
                log.error(e.getMessage());
                throw new GeneralException(e.getMessage(), e.getStatus());
            }
        }).toList();

    }

    @Override
    public BrandResponse getBrandByName(String name) {
        log.info("Fetching brand by name: {}", name);
        Brand brand = brandRepository.findByName(name).orElseThrow(
                () -> new ResourceNotFoundException(BRAND,"Name",name));
        return brandMapper.mapBrandToBrandResponse(brand);
    }

    @Override
    public BrandResponse createBrand(BrandRequest request) {
       try {
           brandRepository.findByName(request.getName()).ifPresent(brand -> {
               log.error("Brand already exists");
               throw new ResourceAlreadyExistsException(BRAND, HttpStatus.CONFLICT);
           });
           log.info("Mapping brand request to brand {}", request);
           Brand brand = brandMapper.mapBrandRequestToBrand(request);
           log.info("Saving brand {}", brand);
           Brand saved = brandRepository.save(brand);
           return brandMapper.mapBrandToBrandResponse(saved);
       }catch (ConversionException e) {
           throw new GeneralException(e.getMessage(), e.getStatus());
       }
    }

    @Override
    public BrandResponse updateBrand(int id, BrandRequest request) {
        try {
            Brand brand = brandRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(BRAND, ID, request.getName()));
            brandRepository.findByName(request.getName()).ifPresent(b -> {
                if (b.getId() != id) {
                    throw new ResourceAlreadyExistsException(BRAND, HttpStatus.CONFLICT);
                }
            });
            Brand toBrand = brandMapper.mapBrandRequestToBrand(request);
            brand.setName(toBrand.getName());
            brand.setImage(toBrand.getImage());
            Brand updated = brandRepository.save(brand);
            return brandMapper.mapBrandToBrandResponse(updated);
        }catch (GeneralException e){
            throw new GeneralException(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @Override
    public BrandResponse deleteBrand(int id) {
        try {
            Brand brand = brandRepository.findById(id).orElseThrow(
                    () -> new ResourceNotFoundException(BRAND, ID, id)
            );
            brand.setDeletedAt(Instant.now());
            Brand saved = brandRepository.save(brand);
            return brandMapper.mapBrandToBrandResponse(saved);
        }catch (ConversionException e){
            throw new GeneralException(e.getMessage(), e.getStatus());
        }
    }

    @Override
    public BrandResponse getBrandById(int id){
        try {
            log.info("Fetching brand by id: {}", id);
            Brand brand = brandRepository.findById(id).orElseThrow(
                    () -> new ResourceNotFoundException(BRAND, ID, id)
            );
            log.info("Mapping brand to brand response, {}", brand.toString());
            return brandMapper.mapBrandToBrandResponse(brand);
        }catch (ConversionException e){
            throw new GeneralException(e.getMessage(), e.getStatus());
        }
    }
}
