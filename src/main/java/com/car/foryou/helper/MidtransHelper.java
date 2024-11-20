package com.car.foryou.helper;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class MidtransHelper {

    private final WebClient webClient;

    public MidtransHelper(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.sandbox.midtrans.com").build();
    }

    public Map<String, Object> callChargeApi(Map<String, Object> paymentRequest){
        String string = paymentRequest.toString();
        System.out.println(string);
        return this.webClient.post()
                .uri("/v2/charge")
                .header("Authorization", "Basic U0ItTWlkLXNlcnZlci1ZMFFzRDlVbnptM25qc1dnOXhzUVBBVnc6")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .body(Mono.just(paymentRequest), Map.class)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }
}
