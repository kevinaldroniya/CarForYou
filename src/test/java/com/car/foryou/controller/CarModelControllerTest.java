package com.car.foryou.controller;

import com.car.foryou.dto.model.CarModelResponse;
import com.car.foryou.model.CarModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CarModelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @ParameterizedTest
    @CsvFileSource(resources = "/car_models_data.csv", numLinesToSkip = 1)
    void testCreateCarModel_shouldReturnCreatedCarModel(String brandName, String modelName) throws Exception{
        //Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("name", modelName);
        request.put("brandName", brandName);
        //Act
        mockMvc.perform(post("/models")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        //Assert
                .andExpect(status().isOk())
                .andDo(result -> {
                    CarModelResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response);
                    assertEquals(request.get("name"), response.getName());
                });
    }

}