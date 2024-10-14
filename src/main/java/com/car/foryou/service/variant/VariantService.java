package com.car.foryou.service.variant;

import com.car.foryou.dto.variant.VariantCriteria;
import com.car.foryou.dto.variant.VariantRequest;
import com.car.foryou.dto.variant.VariantResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface VariantService {
    List<VariantResponse> getAllVariants();
    VariantResponse createVariant(VariantRequest variant);
    VariantResponse updateVariant(int id, VariantRequest variant);
    VariantResponse deleteVariant(int id);
    VariantResponse getVariantByCriteria(@Valid VariantCriteria variant);
}
