package com.car.foryou.controller;

import com.car.foryou.dto.payment.PaymentRequest;
import com.car.foryou.dto.payment.PaymentResponse;
import com.car.foryou.service.payment.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService){
        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments(){
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable("paymentId") Integer paymentId){
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }

    @PostMapping("/{paymentId}")
    public ResponseEntity<String> pay(@PathVariable("paymentId") Integer paymentId, @RequestBody PaymentRequest request){
        return ResponseEntity.ok(paymentService.pay(paymentId, request));
    }

    @PostMapping("/confirm/{paymentId}")
    public ResponseEntity<String> confirm(@PathVariable("paymentId") Integer paymentId){
        return ResponseEntity.ok(paymentService.confirmPayment(paymentId));
    }


}
