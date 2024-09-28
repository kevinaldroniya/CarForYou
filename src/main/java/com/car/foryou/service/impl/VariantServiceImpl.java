package com.car.foryou.service.impl;

import com.car.foryou.dto.variant.VariantRequest;
import com.car.foryou.dto.variant.VariantResponse;
import com.car.foryou.model.CarModel;
import com.car.foryou.model.Variant;
import com.car.foryou.repository.ModelRepository;
import com.car.foryou.repository.VariantRepository;
import com.car.foryou.service.VariantService;
import com.car.foryou.mapper.VariantMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class VariantServiceImpl implements VariantService {

    private final VariantRepository variantRepository;
    private final ModelRepository modelRepository;
    private final VariantMapper variantMapper;

    public VariantServiceImpl(VariantRepository variantRepository, ModelRepository modelRepository, VariantMapper variantMapper) {
        this.variantRepository = variantRepository;
        this.modelRepository = modelRepository;
        this.variantMapper = variantMapper;
    }

    @Override
    public List<VariantResponse> getAllVariants() {
        List<Variant> variants = variantRepository.findAll();
        return variants.stream().map(variantMapper::mapVariantToVariantResponse).toList();
    }

    @Override
    public VariantResponse createVariant(VariantRequest request) {
      try {
          CarModel carModel = modelRepository.findByName(request.getModel()).orElseThrow(() -> new RuntimeException("Model with name " + request.getModel() + " not found"));
          variantRepository.findByNameAndYearAndCarModel(request.getName(), request.getYear(), carModel).ifPresent(model -> {
              throw new RuntimeException("Variant with name " + request.getName() + " and year " + request.getYear() + " already exists");
          });
          Variant mappedToVariant = variantMapper.mapVariantRequestToVariant(request, carModel);
          mappedToVariant.setCreatedBy(1);
          mappedToVariant.setCreatedAt(LocalDateTime.now());
          Variant savedVariant = variantRepository.save(mappedToVariant);
          return variantMapper.mapVariantToVariantResponse(savedVariant);
      }catch (Exception e){
          throw new RuntimeException(e.getMessage());
      }
    }

    @Override
    public VariantResponse updateVariant(int id, VariantRequest request) {
        try {
            Variant variant = variantRepository.findById(id).orElseThrow(
                    () -> new RuntimeException("Variant with id " + id + " not found"));
            CarModel carModel = modelRepository.findByName(request.getModel()).orElseThrow(
                    () -> new RuntimeException("Model with name " + request.getModel() + " not found"));
            variantRepository.findByNameAndYearAndCarModel(request.getName(), request.getYear(), carModel).ifPresent(model -> {
                if (model.getId() != id) {
                    throw new RuntimeException("Variant with name " + request.getName() + " and year " + request.getYear() + " already exists");
                }
            });
            Variant mappedVariantRequestToVariant = variantMapper.mapVariantRequestToVariant(request, carModel);
            variant.setName(mappedVariantRequestToVariant.getName());
            variant.setYear(mappedVariantRequestToVariant.getYear());
            variant.setEngine(mappedVariantRequestToVariant.getEngine());
            variant.setTransmission(mappedVariantRequestToVariant.getTransmission());
            variant.setFuel(mappedVariantRequestToVariant.getFuel());
            variant.setCarModel(mappedVariantRequestToVariant.getCarModel());
            variant.setUpdatedBy(1);
            variant.setUpdatedAt(ZonedDateTime.now(ZoneId.of("UTC")));
            Variant savedVariant = variantRepository.save(variant);
            return variantMapper.mapVariantToVariantResponse(savedVariant);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public VariantResponse deleteVariant(int id) {
        try {
            Variant variant = variantRepository.findById(id).orElseThrow(
                    () -> new RuntimeException("Variant with id " + id + " not found"));
            variant.setDeletedBy(1);
            variant.setDeletedAt(ZonedDateTime.now(ZoneId.of("UTC")));
            Variant saved = variantRepository.save(variant);
            return variantMapper.mapVariantToVariantResponse(saved);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
