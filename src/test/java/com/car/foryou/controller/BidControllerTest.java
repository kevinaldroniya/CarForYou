package com.car.foryou.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BidControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testPlaceBidConcurrent() throws InterruptedException {
        int numberOfThreads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        Integer auctionId = 2;
        Map<String, String> user = getUserV1();
        for (int i = 0; i < numberOfThreads; i++) {
            String userN = "user"+(i+1);
            String jwtToken = user.get(userN);
            executorService.execute(() -> {
                try {
                    mockMvc.perform(post("/bids/" + auctionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", jwtToken))
                            .andExpect(status().isOk());
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
        }
        executorService.shutdown();
        while (!executorService.isTerminated()){
            Thread.sleep(100);
        }
    }

    private Map<String, String> getUserV1(){
        String csvFile = "src/test/resources/auth_response.csv";
        Map<String, String> result = new HashMap<>();
        try(BufferedReader br = new BufferedReader(new FileReader(csvFile))){
            String line;
            while ((line = br.readLine()) != null){
                String[] parts = line.split(",",2);
                if (parts.length == 2){
                    String username = parts[0].replace("\"","");
                    String token = parts[1].replace("\"","");
                    result.put(username, token);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return result;
    }

    private Map<String, String> getUser(){
        return Map.of(
                "user1","eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMSIsImlzVmVyaWZpZWQiOnRydWUsImdyb3VwIjoiVVNFUiIsImlhdCI6MTczMjE3MzA3OSwiZXhwIjoxNzMyMjA5MDc5fQ.axAuT5WwJw7B7v702hiRk6tD-KN4ABD2HDRy478LnAM",
                "user2","eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMiIsImlzVmVyaWZpZWQiOnRydWUsImdyb3VwIjoiVVNFUiIsImlhdCI6MTczMjE3MzA3OSwiZXhwIjoxNzMyMjA5MDc5fQ.pURU0j5ORA0GdVcQpcYtE_LSei14nnV2Q1TRt6Sndro",
                "user3","eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMyIsImlzVmVyaWZpZWQiOnRydWUsImdyb3VwIjoiVVNFUiIsImlhdCI6MTczMjE3MzA3OSwiZXhwIjoxNzMyMjA5MDc5fQ.oyS-QY3CzBHktEllIMWF59MT8GWYTkZgwWWXX_eK_AE",
                "user4","eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyNCIsImlzVmVyaWZpZWQiOnRydWUsImdyb3VwIjoiVVNFUiIsImlhdCI6MTczMjE3MzA3OSwiZXhwIjoxNzMyMjA5MDc5fQ.Jiur1CeTI_V9uLY6n_zsBAY0GHDIljA4OW3rPSUWqY8",
                "user5","eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyNSIsImlzVmVyaWZpZWQiOnRydWUsImdyb3VwIjoiVVNFUiIsImlhdCI6MTczMjE3MzA3OSwiZXhwIjoxNzMyMjA5MDc5fQ.2vpbSreuBuXJZf-B3esIemMFmdaJkk0WYGJg1B6O814",
                "user6","eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyNiIsImlzVmVyaWZpZWQiOnRydWUsImdyb3VwIjoiVVNFUiIsImlhdCI6MTczMjE3MzA4MCwiZXhwIjoxNzMyMjA5MDgwfQ.wANpigACQ2cJi6ZjarsPpVLe53McMccpGDKaCJBOF7I",
                "user7","eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyNyIsImlzVmVyaWZpZWQiOnRydWUsImdyb3VwIjoiVVNFUiIsImlhdCI6MTczMjE3MzA4MCwiZXhwIjoxNzMyMjA5MDgwfQ._99W7wDgy2sQGoGXAlPtOz5dTqj9VRzGkUNL3vCSXnw",
                "user8","eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyOCIsImlzVmVyaWZpZWQiOnRydWUsImdyb3VwIjoiVVNFUiIsImlhdCI6MTczMjE3MzA4MCwiZXhwIjoxNzMyMjA5MDgwfQ.lTL4WZyizFS0rCACGJOhYJh1CkdJvBoZRFjUYaIMTOU",
                "user9","eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyOSIsImlzVmVyaWZpZWQiOnRydWUsImdyb3VwIjoiVVNFUiIsImlhdCI6MTczMjE3MzA4MCwiZXhwIjoxNzMyMjA5MDgwfQ.mgdKhcGo6bnIaFOcmKFQk2to8RJXXZnYT0AvuQy2RQ8",
                "user10","eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMTAiLCJpc1ZlcmlmaWVkIjp0cnVlLCJncm91cCI6IlVTRVIiLCJpYXQiOjE3MzIxNzMwODAsImV4cCI6MTczMjIwOTA4MH0.g8A9aKpoAGCGmEfvhvzGrC_xv3QUdLMo97bBq2sCLBw"
        );
    }

}