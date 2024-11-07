package com.car.foryou.controller;

import com.car.foryou.dto.GeneralResponse;
import com.car.foryou.dto.payment.PaymentCallbackRequest;
import com.car.foryou.dto.payment.PaymentRequest;
import com.car.foryou.dto.payment.PaymentResponse;
import com.car.foryou.service.payment.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController  {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService){
        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments(){
        return ResponseEntity.ok(paymentService.getAllPaymentsResponse());
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable("paymentId") Integer paymentId){
        return ResponseEntity.ok(paymentService.getPaymentResponseById(paymentId));
    }

    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
    @PostMapping("/{paymentId}")
    public ResponseEntity<GeneralResponse<String>> pay(@PathVariable("paymentId") Integer paymentId, @RequestBody PaymentRequest request){
        return ResponseEntity.ok(paymentService.manualPayment(paymentId, request));
    }

    @PreAuthorize("hasAnyRole('ADMIN','AUCTIONEER')")
    @PostMapping("/confirm/{paymentId}")
    public ResponseEntity<GeneralResponse<String>> confirm(@PathVariable("paymentId") Integer paymentId){
        return ResponseEntity.ok(paymentService.confirmPayment(paymentId));
    }

    @PostMapping("/callback")
    public ResponseEntity<GeneralResponse<Map<String, Object>>> callback(@RequestParam(name = "return", required = true) Boolean result ,
                                                                         @RequestParam(name = "sid", required = true) String sid,
                                                                         @RequestParam(name = "trx_id", required = true) String trxId,
                                                                         @RequestParam(name = "status", required = true) String status,
                                                                         @RequestParam(name = "tipe", required = true) String tipe,
                                                                         @RequestParam(name = "payment_method", required = true) String paymentMethod,
                                                                         @RequestParam(name = "payment_channel", required = true) String paymentChannel) {
        PaymentCallbackRequest callbackRequest = PaymentCallbackRequest.builder()
                .result(result)
                .sid(sid)
                .trxId(trxId)
                .status(status)
                .tipe(tipe)
                .paymentMethod(paymentMethod)
                .paymentChannel(paymentChannel)
                .build();
        return ResponseEntity.ok(paymentService.callback(callbackRequest));
    }

    @PostMapping("/notifyUrl")
    public ResponseEntity<String> notifyUrl() {
        return ResponseEntity.ok("MANGEAK");
    }


}
