package com.car.foryou.service.variant;

import com.car.foryou.dto.variant.VariantCriteria;
import com.car.foryou.dto.variant.VariantRequest;
import com.car.foryou.dto.variant.VariantResponse;
import com.car.foryou.exception.ResourceNotFoundException;
import com.car.foryou.model.CarModel;
import com.car.foryou.model.Variant;
import com.car.foryou.repository.model.ModelRepository;
import com.car.foryou.repository.variant.VariantRepository;
import com.car.foryou.repository.variant.VariantSpecifications;
import com.car.foryou.mapper.VariantMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.List;

@Service
@Validated
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
            Variant updateVariant = updateVariant(variant, mappedVariantRequestToVariant);
            Variant savedVariant = variantRepository.save(updateVariant);
            return variantMapper.mapVariantToVariantResponse(savedVariant);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

private Variant updateVariant(Variant variant, Variant request){
        variant.setName(request.getName());
        variant.setYear(request.getYear());
        variant.setEngine(request.getEngine());
        variant.setTransmission(request.getTransmission());
        variant.setFuel(request.getFuel());
        variant.setCarModel(request.getCarModel());
        return variant;
    }

    @Override
    public VariantResponse deleteVariant(int id) {
        try {
            Variant variant = variantRepository.findById(id).orElseThrow(
                    () -> new RuntimeException("Variant with id " + id + " not found"));
            variant.setDeletedAt(Instant.now());
            Variant saved = variantRepository.save(variant);
            return variantMapper.mapVariantToVariantResponse(saved);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public VariantResponse getVariantByCriteria(VariantCriteria variant) {
        Variant found = variantRepository.findOne(VariantSpecifications.hasCriteria(variant)).orElseThrow(
                () -> new ResourceNotFoundException("Variant", "criteria", variant.toString())
        );
        return variantMapper.mapVariantToVariantResponse(found);
    }
}
