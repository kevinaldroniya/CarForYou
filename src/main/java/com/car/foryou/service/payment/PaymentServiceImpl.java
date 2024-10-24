package com.car.foryou.service.payment;

import com.car.foryou.dto.bid.BidDetailResponse;
import com.car.foryou.dto.bid.BidStatus;
import com.car.foryou.dto.payment.PaymentRequest;
import com.car.foryou.dto.payment.PaymentStatus;
import com.car.foryou.exception.InvalidRequestException;
import com.car.foryou.exception.ResourceNotFoundException;
import com.car.foryou.model.BidDetail;
import com.car.foryou.model.PaymentDetail;
import com.car.foryou.repository.payment.PaymentRepository;
import com.car.foryou.service.bid.BidService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRepository paymentRepository;
    private final BidService bidService;

    public PaymentServiceImpl(PaymentRepository paymentRepository, BidService bidService) {
        this.paymentRepository = paymentRepository;
        this.bidService = bidService;
    }

    @Override
    public String pay(Integer bidId, PaymentRequest paymentRequest) {
        BidDetailResponse foundedBid = bidService.getBidDetailById(bidId);
        if (!foundedBid.getBidStatus().equals(BidStatus.WAITING_FOR_PAYMENT)){
            throw new InvalidRequestException("Not Found", HttpStatus.BAD_REQUEST);
        }

        return "";
    }

    @Override
    public void setPaymentDetail(PaymentRequest paymentRequest) {
        BidDetailResponse foundedBid = bidService.getBidDetailById(paymentRequest.getBidId());
        if (!foundedBid.getBidStatus().equals(BidStatus.WAITING_FOR_CONFIRMATION)){
            throw new InvalidRequestException("Invalid", HttpStatus.BAD_REQUEST);
        }
        PaymentDetail paymentDetail = PaymentDetail.builder()
                .paymentStatus(PaymentStatus.PENDING)
                .userId(Integer.valueOf(foundedBid.getBidder()))
                .paymentAmount(foundedBid.getBidAmount())
                .bidDetail(BidDetail.builder().id(foundedBid.getBidId()).build())
                .build();
        paymentRepository.save(paymentDetail);
    }

}
