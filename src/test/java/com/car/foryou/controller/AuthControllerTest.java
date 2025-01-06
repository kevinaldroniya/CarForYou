package com.car.foryou.controller;

import com.car.foryou.dto.GeneralResponse;
import com.car.foryou.dto.auth.AuthResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void setupCSV() {
        File csvFile = new File("src/test/resources/auth_response.csv");

        // Check if the file exists and truncate it if it does
        if (csvFile.exists()) {
            try (PrintWriter pw = new PrintWriter(csvFile)) {
                pw.print(""); // Empty the file
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    @ParameterizedTest
//    @CsvFileSource(resources = "/user_registration_request.csv", numLinesToSkip = 1)
//    void testRegisterUser(String email, String username, String phoneNumber, String firstName, String lastName, String password) throws Exception {
////    @Test
////        void testRegister() throws Exception {
//        //Arrange
//        Map<String, Object> request = Map.of(
//                "email", email,
//                "username", username,
//                "phoneNumber", phoneNumber,
//                "firstName", firstName,
//                "lastName", lastName,
//                "password", password
//        );
//        //Act
//        mockMvc.perform(post("/auth/register")
//                        .accept(MediaType.APPLICATION_JSON)
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(request)))
//        //Assert
//                .andExpect(status().isCreated())
//                .andDo(result -> {
//                    GeneralResponse<String> response = objectMapper.readValue(
//                            result.getResponse().getContentAsString(), new TypeReference<>() {
//                            });
//                    assertNotNull(response);
//                });
//    }

    @RepeatedTest(100)
    void testLogin(RepetitionInfo repetitionInfo) throws Exception {
        Map<String, Object> request = Map.of(
                "identifier", "user" + repetitionInfo.getCurrentRepetition(),
                "password", "user"
        );
        Random random = new Random();
        long amount = random.nextLong(800_000_000, 1_000_000_000);
        String username = (String) request.get("identifier");
        mockMvc.perform(post("/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    AuthResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response);
                    // Convert AuthResponse to CSV

                    // Prepare data for CSV (you can dynamically extract response fields)
                    String[] csvData = new String[]{
                            username,  // Use dynamic username
                            "Bearer " + response.getAccessToken(),  // Extract the access token
                            Long.toString(amount)
                    };


                    Path path = Paths.get("src/test/resources/auth_response.csv");
                    try (CSVWriter writer = new CSVWriter(new FileWriter(path.toFile(), true))) {
//                            writer.writeNext(new String[]{"Username", "Access_Token"}); // header (only for first test case)
                        writer.writeNext(csvData); // data row
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

//                        // Write data to CSV (appending new lines for each test case)
//                        try (CSVWriter writer = new CSVWriter(new FileWriter("src/test/resources/auth_response.csv", true),
//                                CSVWriter.DEFAULT_SEPARATOR,
//                                CSVWriter.NO_QUOTE_CHARACTER,
//                                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
//                                CSVWriter.DEFAULT_LINE_END)) {
//                            // If the file is empty, you could still add the header here as before
//                            writer.writeNext(csvData); // Write data row
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                });
    }


}