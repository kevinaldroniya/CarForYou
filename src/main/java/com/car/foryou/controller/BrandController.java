package com.car.foryou.controller;

import com.car.foryou.dto.brand.BrandRequest;
import com.car.foryou.dto.brand.BrandResponse;
import com.car.foryou.service.BrandService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brands")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public ResponseEntity<List<BrandResponse>> getAllBrands(){
        return ResponseEntity.ok(brandService.getBrands());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandResponse> getBrandsById(@PathVariable("id") Integer id){
        return ResponseEntity.ok(brandService.getBrandById(id));
    }

    @PostMapping
    public ResponseEntity<BrandResponse> createBrand(@RequestBody BrandRequest request){
        return new ResponseEntity<>(brandService.createBrand(request), HttpStatus.CREATED);
    }

    @PutMapping(
            path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BrandResponse> editBrandById(@PathVariable("id") int id, @RequestBody BrandRequest request){
        return new ResponseEntity<>(brandService.updateBrand(id, request), HttpStatus.OK);
    }

    @DeleteMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BrandResponse> deleteBrandById(@PathVariable("id") int id){
        return new ResponseEntity<>(brandService.deleteBrand(id), HttpStatus.OK);
    }
}
