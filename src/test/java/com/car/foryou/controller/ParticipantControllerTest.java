package com.car.foryou.controller;

import com.car.foryou.dto.participant.ParticipantResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ParticipantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Disabled
    @ParameterizedTest
    @CsvFileSource(resources = "/auth_response.csv")
    void testJoinAuction(String username, String jwtToken) throws Exception {
        mockMvc.perform(post("/participants/auction/" + 1)
                .header("Authorization", jwtToken)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(result -> {
                    ParticipantResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response);
                    String[] csvData = new String[]{
                            response.getParticipantId().toString(),
                            response.getUserId().toString(),
                            jwtToken
                    };

                    try(CSVWriter writer = new CSVWriter(new FileWriter("src/test/resources/participant_join_response.csv", true),
                            CSVWriter.DEFAULT_SEPARATOR,
                            CSVWriter.NO_QUOTE_CHARACTER,
                            CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                            CSVWriter.DEFAULT_LINE_END)){
                        writer.writeNext(csvData);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                });
    }
}