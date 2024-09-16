package com.car.foryou.controller;

import com.car.foryou.dto.variant.VariantRequest;
import com.car.foryou.dto.variant.VariantResponse;
import com.car.foryou.service.VariantService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/variants")
public class VariantController {

    private final VariantService variantService;

    public VariantController(VariantService variantService) {
        this.variantService = variantService;
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<VariantResponse>> getAllVariants(){
        List<VariantResponse> response = variantService.getAllVariants();
        return ResponseEntity.ok(response);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<VariantResponse> createVariant(@RequestBody VariantRequest variant){
        VariantResponse response = variantService.createVariant(variant);
        return ResponseEntity.ok(response);
    }

    @PutMapping(
            path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<VariantResponse> updateVariant(@PathVariable("id") long id, @RequestBody VariantRequest variant){
        VariantResponse response = variantService.updateVariant(id, variant);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<VariantResponse> deleteVariant(@PathVariable("id") long id){
        VariantResponse response = variantService.deleteVariant(id);
        return ResponseEntity.ok(response);
    }
}
