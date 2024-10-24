package com.car.foryou.service.payment;

import com.car.foryou.dto.bid.BidDetailResponse;
import com.car.foryou.dto.bid.BidStatus;
import com.car.foryou.dto.item.ItemResponse;
import com.car.foryou.dto.item.ItemStatus;
import com.car.foryou.dto.payment.PaymentRequest;
import com.car.foryou.dto.payment.PaymentSetRequest;
import com.car.foryou.dto.payment.PaymentStatus;
import com.car.foryou.dto.user.UserResponse;
import com.car.foryou.exception.InvalidRequestException;
import com.car.foryou.exception.ResourceNotFoundException;
import com.car.foryou.model.BidDetail;
import com.car.foryou.model.Item;
import com.car.foryou.model.PaymentDetail;
import com.car.foryou.model.User;
import com.car.foryou.repository.payment.PaymentRepository;
import com.car.foryou.service.auctionparticipant.AuctionParticipantService;
import com.car.foryou.service.bid.BidService;
import com.car.foryou.service.item.ItemService;
import com.car.foryou.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRepository paymentRepository;
    private final ItemService itemService;
    private final UserService userService;
    private final AuctionParticipantService auctionParticipantService;

    public PaymentServiceImpl(PaymentRepository paymentRepository, ItemService itemService, UserService userService, AuctionParticipantService auctionParticipantService) {
        this.paymentRepository = paymentRepository;
        this.itemService = itemService;
        this.userService = userService;
        this.auctionParticipantService = auctionParticipantService;
    }

    @Override
    public PaymentDetail getPaymentById(Integer id) {
        PaymentDetail paymentDetail = paymentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Payment", "Id", id)
        );
        return paymentDetail;
    }

    @Override
    public String pay(Integer paymentId, PaymentRequest paymentRequest) {
        PaymentDetail paymentDetail = paymentRepository.findById(paymentId).orElseThrow(
                () -> new ResourceNotFoundException("Payment", "Id", paymentId)
        );
        if (!paymentDetail.getPaymentStatus().equals(PaymentStatus.PENDING)&& paymentDetail.getPaymentExpiration().isBefore(Instant.now())){
            throw new InvalidRequestException("Invalid payment", HttpStatus.BAD_REQUEST);
        }
        if (!paymentDetail.getPaymentAmount().equals(paymentRequest.getPaymentAmount())){
            throw new InvalidRequestException("Payment amount does not match", HttpStatus.BAD_REQUEST);
        }
        paymentDetail.setPaymentMethod(paymentRequest.getPaymentMethod());
        paymentDetail.setShippingAddress(paymentRequest.getShippingAddress());
        paymentDetail.setShippingProvince(paymentRequest.getShippingProvince());
        paymentDetail.setShippingCity(paymentRequest.getShippingCity());
        paymentDetail.setShippingPostalCode(paymentRequest.getShippingPostalCode());
        paymentDetail.setPaymentStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(paymentDetail);
        return "Payment successful";
    }

    @Override
    public void setPaymentDetail(PaymentSetRequest request) {
        UserResponse userResponse = userService.getUserById(request.getUserId());
        ItemResponse itemResponse = itemService.getItemById(request.getItemId());
        User user = User.builder().id(userResponse.getId()).build();
        Item item = Item.builder().id(itemResponse.getItemId()).build();
        paymentRepository.findByUserAndItem(user, item).ifPresent(
                paymentDetail -> {
                    throw new InvalidRequestException("Payment already done for this item", HttpStatus.BAD_REQUEST);
                }
        );
        PaymentDetail paymentDetail = PaymentDetail.builder()
                .user(user)
                .item(item)
                .paymentAmount(request.getPaymentAmount())
                .paymentStatus(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(paymentDetail);
    }

    @Override
    public String confirmPayment(Integer paymentId) {
        PaymentDetail paymentDetail = paymentRepository.findById(paymentId).orElseThrow(
                () -> new ResourceNotFoundException("Payment", "Id", paymentId)
        );
        paymentDetail.setPaymentStatus(PaymentStatus.CONFIRMED);
        paymentRepository.save(paymentDetail);
        itemService.updateItemStatus(paymentDetail.getId(), ItemStatus.SOLD);
        auctionParticipantService.setWinner(paymentDetail.getUser().getId(), paymentDetail.getItem().getId());
        return "Payment confirmed";
    }

}
