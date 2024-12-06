package com.car.foryou.controller;

import com.car.foryou.dto.GeneralResponse;
import com.car.foryou.dto.payment.PaymentRequest;
import com.car.foryou.dto.payment.PaymentResponse;
import com.car.foryou.service.payment.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
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

//    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
//    @PostMapping("/{paymentId}")
//    public ResponseEntity<GeneralResponse<String>> pay(@PathVariable("paymentId") Integer paymentId, @RequestBody PaymentRequest request){
//        return ResponseEntity.ok(paymentService.manualPayment(paymentId, request));
//    }

//    @PreAuthorize("hasAnyRole('ADMIN','AUCTIONEER')")
//    @PostMapping("/confirm/{paymentId}")
//    public ResponseEntity<GeneralResponse<String>> confirm(@PathVariable("paymentId") Integer paymentId){
//        return ResponseEntity.ok(paymentService.confirmPayment(paymentId));
//    }

    @PostMapping("/pay/now")
    public ResponseEntity<PaymentResponse> payNow(@RequestBody PaymentRequest request){
        return new ResponseEntity<>(paymentService.payOnline(request), HttpStatus.CREATED);
    }

    @PostMapping("/callback/notification")
    public ResponseEntity<PaymentResponse> callbackNotification(@RequestBody Map<String, Object> payload, HttpServletRequest servletRequest){
        return new ResponseEntity<>(paymentService.callbackNotification(payload, servletRequest), HttpStatus.OK);
    }


}
