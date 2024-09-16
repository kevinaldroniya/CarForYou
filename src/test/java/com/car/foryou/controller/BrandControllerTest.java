package com.car.foryou.controller;

import com.car.foryou.dto.brand.BrandRequest;
import com.car.foryou.dto.brand.BrandResponse;
import com.car.foryou.model.Image;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;


import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BrandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @ParameterizedTest
    @CsvFileSource(resources = "/car_brands_data.csv", numLinesToSkip = 1)
    void testCreateBrand_shouldReturnCreatedBrand(String name) throws Exception{
        //Arrange
        Image image = Image.builder()
                .imageId("1")
                .large("100px")
                .medium("50px")
                .small("25px")
                .build();
        BrandRequest brandRequest = BrandRequest.builder()
                .name(name)
                .image(image)
                .build();

        Map<String, Object> brandRequestMap = Map.of(
                "name", brandRequest.getName(),
                "image", brandRequest.getImage()
        );
        //Act
        mockMvc.perform(post("/brands")
                .accept("application/json")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(brandRequestMap)))
                //Assert
                .andExpect(status().isCreated())
                .andDo(result -> {
                    BrandResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response);
                    assertEquals(brandRequest.getName(), response.getName());
                    assertEquals(brandRequest.getImage().getImageId(), response.getImage().getImageId());
                });
    }

}