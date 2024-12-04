package com.car.foryou.controller;

import com.car.foryou.model.Bid;
import com.car.foryou.repository.bid.BidRepository;
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

    @Autowired
    private BidRepository bidRepository;

    @Test
    void testPlaceBidConcurrent() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        Integer auctionId = 3;
        Map<String, Object> user = getUserV1();
        for (int i = 0; i < 5000; i++) {
            Bid bid = bidRepository.findHighestBidByAuctionId(auctionId).orElse(null);
            String highestBidUserId = bid != null ? bid.getUser().getId().toString() : "0";
            Long highestBidAmount = bid != null? bid.getBidAmount() : 0L;
//            System.out.println(Thread.currentThread().getId());
//            long userId = Thread.currentThread().getId();

//            if (highestBidUserId.equals(userN)){
//                System.out.println(String.format("%s has the biggest bid : %d", userN, highestBidAmount));
//                continue;
//            }else if (amount < highestBidAmount){
//                System.out.println(String.format("%s amount:%d are smaller than highestBid : %d", userN, amount, highestBidAmount));
//                continue;
//            }
            executorService.execute(() -> {
                String name = Thread.currentThread().getName();
                String[] splitThread = Thread.currentThread().getName().split("-");
                String userId = splitThread[splitThread.length-1];
                String userN = "user"+(userId);
                Map<String, Object> userData = (Map<String, Object>) user.get(userN);
                String jwtToken = (String) userData.get("token");
                Integer amount = (Integer) userData.get("amount");
                System.out.println(name);
                if (highestBidUserId.equals(userN)){
                    System.out.println(String.format("%s has the biggest bid : %d", userN, highestBidAmount));
                    return;
                }else if (amount < highestBidAmount){
                    System.out.println(String.format("%s amount:%d are smaller than highestBid : %d", userN, amount, highestBidAmount));
                    return;
                }
                try {
                    Thread.sleep(10_000);
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

    private Map<String, Object> getUserV1(){
        String csvFile = "src/test/resources/auth_response.csv";
        Map<String, Object> result = new HashMap<>();
        try(BufferedReader br = new BufferedReader(new FileReader(csvFile))){
            String line;
            while ((line = br.readLine()) != null){
                String[] parts = line.split(",",3);
                if (parts.length == 3){
                    String username = parts[0].replace("\"","");
                    String token = parts[1].replace("\"","");
                    Integer amount = Integer.valueOf(parts[2].replace("\"",""));

                    result.put(username,  Map.of("token", token,
                            "amount", amount));
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return result;
    }

}