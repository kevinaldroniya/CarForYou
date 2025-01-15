package com.car.foryou.controller;

import com.car.foryou.dto.payment.PaymentMethod;
import com.car.foryou.dto.payment.PaymentRequest;
import com.car.foryou.dto.payment.PaymentResponse;
import com.car.foryou.dto.payment.PaymentType;
import com.car.foryou.exception.InvalidRequestException;
import com.car.foryou.helper.EncryptionHelper;
import com.car.foryou.helper.MidtransHelper;
import com.car.foryou.model.Auction;
import com.car.foryou.model.Participant;
import com.car.foryou.repository.participant.ParticipantRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerTest {

    private static final Logger log = LoggerFactory.getLogger(PaymentControllerTest.class);
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MidtransHelper midtransHelper;

    @MockBean
    private EncryptionHelper encryptionHelper;

    @Autowired
    private ParticipantRepository participantRepository;


    @BeforeAll
    static void setupCSV(){
        File csvFile = new File("src/test/resources/deposit_payment.csv");

        // Check if the file exists and truncate it if it does
        if (csvFile.exists()) {
            try (PrintWriter pw = new PrintWriter(csvFile)) {
                pw.print(""); // Empty the file
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Disabled
    @ParameterizedTest
    @CsvFileSource(resources = "/participant_join_response.csv")
    void testPayNowDeposit_withMockMidtrans(Integer participantId, Integer userId, String jwtToken) throws Exception {
        Random random = new Random();
        List<String> paymentChannels = List.of(
                "bca",
                "bri",
                "bni",
                "cimb",
                "permata",
                "mandiri"
        );
        Participant participant = participantRepository.findById(participantId).orElseThrow();
        Auction auction = participant.getAuction();
        Instant endDate = auction.getEndDate();
        ZonedDateTime expired = ZonedDateTime.ofInstant(endDate, ZoneId.of("Asia/Jakarta"));
        Double depositAmount = Double.valueOf(auction.getDepositAmount());
        String stringDeposit = String.format("%.2f", depositAmount);
        String paymentChannel = paymentChannels.get(random.nextInt(0, paymentChannels.size()));
        PaymentRequest request = PaymentRequest.builder()
                .paymentType(PaymentType.DEPOSIT)
                .participantId(participantId)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentChannel(paymentChannel)
                .build();

        Map<String, Object> mtMockResponse = getMtMockResponse(paymentChannel, participantId, stringDeposit, expired);
        String transactionId = (String) mtMockResponse.get("transaction_id");
        String code = (String) mtMockResponse.get("code");
        mtMockResponse.remove("code");
        Mockito.when(midtransHelper.callChargeApi(Mockito.anyMap())).thenReturn(mtMockResponse);
        mockMvc.perform(post("/payments/pay/now")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", jwtToken)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(result -> {
                    PaymentResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response);
                    log.info("response : {}", response);
                    String[] csvData = new String[]{
                            response.getPaymentId().toString(),
                            response.getUserId().toString(),
                            response.getOrderId(),
                            transactionId,
                            stringDeposit,
                            paymentChannel,
                            code
                    };
                    try(CSVWriter writer = new CSVWriter(new FileWriter("src/test/resources/deposit_payment.csv", true),
                            CSVWriter.DEFAULT_SEPARATOR,
                            CSVWriter.NO_QUOTE_CHARACTER,
                            CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                            CSVWriter.DEFAULT_LINE_END
                            )){
                        writer.writeNext(csvData);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                });
        Mockito.verify(midtransHelper, Mockito.times(1)).callChargeApi(Mockito.anyMap());
    }

    private String generateOrderId(String paymentType, Integer participantId){
        DateTimeFormatter orderIdFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        ZonedDateTime timeUtc = ZonedDateTime.now(ZoneId.of("UTC"));
        String stringTime = timeUtc.format(orderIdFormatter);
        return String.format("%s-%d-%s", paymentType, participantId, stringTime);
    }

    private Map<String, Object> getMtMockResponse(String paymentChannel, Integer participantId, String grossAmount, ZonedDateTime expired) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Random random = new Random();
        UUID randomUUID = UUID.randomUUID();
        String stringUUID = randomUUID.toString();
        String orderId = generateOrderId("deposit", participantId);
        String vaNumber = String.valueOf(random.nextInt(1_000_000, 9_999_999));
        Map<String, Object> result = new HashMap<>();
        result.put("status_code", "201");
        result.put("status_message", "Success, Bank Transfer transaction is created");
        result.put("transaction_id", stringUUID);
        result.put("order_id", orderId);
        result.put("merchant_id", "123456");
        result.put("gross_amount", grossAmount);
        result.put("currency","IDR");
        result.put("payment_type", "bank_transfer");
        result.put("transaction_time", LocalDateTime.now().format(formatter));
        result.put("transaction_status","pending");
        result.put("fraud_status", "accept");
        result.put("expiry_time", expired.toLocalDateTime().format(formatter));
        switch (paymentChannel.toLowerCase()){
            case "bca", "bni", "bri", "cimb" :
                List<Map<String, Object>> va = List.of(Map.of(
                        "bank", paymentChannel.toLowerCase(),
                        "va_number", vaNumber
                ));
                result.put("va_numbers", va);
                result.put("code", vaNumber);
                break;
            case "permata":
                result.put("code", vaNumber);
                result.put("permata_va_number", vaNumber);
                break;
            case "mandiri" :
                int intBillCode = random.nextInt(70_000, 79_999);
                String strBillCode = String.valueOf(intBillCode);
                result.put("biller_code", strBillCode);
                result.put("bill_key",vaNumber);
                result.put("code", String.format("%s-%s", vaNumber, strBillCode));
                break;
            default:
                throw new InvalidRequestException("invalid", HttpStatus.BAD_REQUEST);
        }
        return result;
    }

    @Disabled
    @ParameterizedTest
    @CsvFileSource(resources = "/deposit_payment.csv")
    void testCallBackNotification(String paymentId, String userId, String orderId, String transactionId, String amount, String paymentChannel, String vaNumber) throws Exception {
        Map<String, Object> request = getCallbackRequest(transactionId, orderId, amount, paymentChannel, vaNumber);
        String signature = "sign-key-x";
        Mockito.when(encryptionHelper.generateSignatureSHA512(Mockito.anyString())).thenReturn(signature);
        mockMvc.perform(post("/payments/callback/notification")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    PaymentResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<PaymentResponse>() {});
                });
    }

    private Map<String, Object> getCallbackRequest(String transactionId, String orderId, String grossAmount, String paymentChannel, String vaNumber){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Map<String, Object> result = new HashMap<>();
        result.put("transaction_time","2024-12-02 15:51:08");
        result.put("transaction_status","settlement");
        result.put("transaction_id", transactionId);
        result.put("status_message", "midtrans payment notification");
        result.put("status_code","200");
        result.put("signature_key","sign-key-x");
        result.put("settlement_time",LocalDateTime.now().format(formatter));
        result.put("order_id", orderId);
        result.put("merchant_id", "G322662765");
        result.put("gross_amount", grossAmount);
        result.put("fraud_status", "accept");
        result.put("expiry_time", "2024-12-03 15:28:40");
        result.put("currency","IDR");

        switch (paymentChannel.toLowerCase()){
            case "bca", "bni", "bri", "cimb" :
                result.put("payment_type", "bank_transfer");
                List<Map<String, Object>> va = List.of(Map.of(
                        "bank", paymentChannel.toLowerCase(),
                        "va_number", vaNumber
                ));
                result.put("va_numbers", va);
                break;
            case "permata":
                result.put("payment_type", "bank_transfer");
                result.put("permata_va_number", vaNumber);
                break;
            case "mandiri" :
                result.put("payment_type", "echannel");
                String[] split = vaNumber.split("-");
                result.put("biller_code",split[0]);
                result.put("bill_key",split[1]);
                break;
            default:
                throw new InvalidRequestException("invalid", HttpStatus.BAD_REQUEST);
        }

        return result;
    }


}