package com.car.foryou.controller;

import com.car.foryou.dto.user.UserInfoDetails;
import com.car.foryou.dto.variant.VariantResponse;
import com.car.foryou.service.auth.JwtService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class VariantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @Disabled
    @ParameterizedTest
    @CsvFileSource(resources = "/car_variants_data.csv")
    void testCreateVariant_shouldReturnCreatedVariant(String modelName, String name, String year, String engines, String transmissions, String fuels) throws Exception{
//    @Test
//    void testCreateVariant_shouldReturnCreatedVariant() throws Exception{
        //Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("name",name);
        request.put("model",modelName);
        request.put("year",Integer.parseInt(year));
        request.put("engine", Set.of(engines.split(",")));
        request.put("transmission", Set.of(transmissions.split(",")));
        request.put("fuel", Set.of(fuels.split(",")));


        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ADMIN"));

        UserInfoDetails userInfoDetails = UserInfoDetails.builder()
                .username("koizaken")
                .password("test")
                .authorities(authorities)
                .build();

        String jwtToken = jwtService.generateToken(userInfoDetails, true);

        //Act
        mockMvc.perform(post("/variants")
                        .header("Authorization", "Bearer " + jwtToken)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        //Assert
                .andExpect(status().isOk())
                .andDo(result -> {
                    VariantResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response);
                    assertEquals(request.get("name"), response.getName());
                });
    }
}