package com.car.foryou.service.payment;

import com.car.foryou.dto.GeneralResponse;
import com.car.foryou.dto.auctionparticipant.AuctionParticipantResponse;
import com.car.foryou.dto.item.ItemResponse;
import com.car.foryou.dto.item.ItemStatus;
import com.car.foryou.dto.notification.MessageTemplate;
import com.car.foryou.dto.notification.NotificationChannel;
import com.car.foryou.dto.payment.*;
import com.car.foryou.dto.user.UserResponse;
import com.car.foryou.exception.InvalidRequestException;
import com.car.foryou.exception.ResourceNotFoundException;
import com.car.foryou.mapper.PaymentMapper;
import com.car.foryou.model.Item;
import com.car.foryou.model.PaymentDetail;
import com.car.foryou.model.User;
import com.car.foryou.repository.payment.PaymentRepository;
import com.car.foryou.service.auctionparticipant.AuctionParticipantService;
import com.car.foryou.service.item.ItemService;
import com.car.foryou.service.notification.NotificationService;
import com.car.foryou.service.user.CustomUserDetailService;
import com.car.foryou.service.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRepository paymentRepository;
    private final ItemService itemService;
    private final UserService userService;
    private final AuctionParticipantService auctionParticipantService;
    private final NotificationService notificationService;

    private static final String PAYMENT = "Payment";

    public PaymentServiceImpl(PaymentRepository paymentRepository, ItemService itemService, UserService userService, AuctionParticipantService auctionParticipantService, NotificationService notificationService) {
        this.paymentRepository = paymentRepository;
        this.itemService = itemService;
        this.userService = userService;
        this.auctionParticipantService = auctionParticipantService;
        this.notificationService = notificationService;
    }

    @Override
    public List<PaymentDetail> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public PaymentDetail getPaymentById(Integer id) {
        return paymentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(PAYMENT, "ID", id)
        );
    }

    @Override
    public PaymentResponse getPaymentResponseById(Integer id) {
        PaymentDetail paymentDetail = getPaymentById(id);
        return PaymentMapper.mapToResponse(paymentDetail);
    }

    @Override
    public GeneralResponse<String> manualPayment(Integer paymentId, PaymentRequest paymentRequest) {
        PaymentDetail paymentDetail = getPaymentById(paymentId);
        if (!paymentDetail.getPaymentStatus().equals(PaymentStatus.PENDING) || paymentDetail.getPaymentExpiration().isBefore(Instant.now())){
            throw new InvalidRequestException("Invalid payment", HttpStatus.BAD_REQUEST);
        }
        if (!paymentDetail.getPaymentAmount().equals(paymentRequest.getPaymentAmount())){
            throw new InvalidRequestException("Payment amount does not match", HttpStatus.BAD_REQUEST);
        }
        paymentDetail.setPaymentMethod(paymentRequest.getPaymentMethod());
//        paymentDetail.setShippingAddress(paymentRequest.getShippingAddress());
//        paymentDetail.setShippingProvince(paymentRequest.getShippingProvince());
//        paymentDetail.setShippingCity(paymentRequest.getShippingCity());
//        paymentDetail.setShippingPostalCode(paymentRequest.getShippingPostalCode());
        paymentDetail.setPaymentStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(paymentDetail);
        return GeneralResponse.<String>builder()
                .message("Payment successful")
                .data(null)
                .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
                .build();
    }

    @Override
    public void setPaymentDetail(PaymentSetRequest request) {
        UserResponse userResponse = userService.getUserResponseById(request.getUserId());
        ItemResponse itemResponse = itemService.getItemResponseById(request.getItemId());
        User user = User.builder().id(userResponse.getId()).build();
        Item item = Item.builder().id(itemResponse.getItemId()).build();
        paymentRepository.findByUserIdAndItemId(user.getId(), item.getId()).ifPresent(
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

    @Transactional
    @Override
    public GeneralResponse<String> confirmPayment(Integer paymentId) {
        PaymentDetail paymentDetail = getPaymentById(paymentId);
        paymentDetail.setPaymentStatus(PaymentStatus.CONFIRMED);
        paymentRepository.save(paymentDetail);
        itemService.updateItemStatus(paymentDetail.getId(), ItemStatus.SOLD);
        auctionParticipantService.setWinner(paymentDetail.getUser().getId(), paymentDetail.getItem().getId());
        auctionParticipantService.bulkRefundDeposit(paymentDetail.getItem().getId());
        return GeneralResponse.<String>builder()
                .message("Payment confirmed")
                .data(null)
                .timestamp(ZonedDateTime.now(ZoneId.of("UTC"))).build();
    }

    @Override
    public PaymentResponse getPaymentByUserIdAndItemId(Integer userId, Integer itemId) {
        PaymentDetail paymentDetail = paymentRepository.findByUserIdAndItemId(userId, itemId).orElseThrow(
                () -> new ResourceNotFoundException(PAYMENT, "UserId or ItemId", userId+"/"+itemId)
        );
        return PaymentMapper.mapToResponse(paymentDetail);
    }

    @Override
    public PaymentResponse updatePaymentStatus(Integer paymentId, PaymentStatus paymentStatus) {
        PaymentDetail paymentDetail = paymentRepository.findById(paymentId).orElseThrow(
                () -> new ResourceNotFoundException(PAYMENT, "ID", paymentId)
        );
        if (paymentStatus.equals(PaymentStatus.CANCELLED) && paymentDetail.getPaymentExpiration().isAfter(Instant.now())){
            throw new InvalidRequestException("Payment cannot be cancelled", HttpStatus.BAD_REQUEST);
        }
        paymentDetail.setPaymentStatus(paymentStatus);
        PaymentDetail save = paymentRepository.save(paymentDetail);
        return PaymentMapper.mapToResponse(save);
    }

    @Override
    public PaymentResponse cancelPayment(PaymentCancelRequest request) {
        PaymentDetail paymentDetail = getPaymentById(request.getBidId());
        if (!paymentDetail.getPaymentStatus().equals(PaymentStatus.PENDING)){
            throw new InvalidRequestException("Payment cannot be cancelled", HttpStatus.BAD_REQUEST);
        }
        if (!CustomUserDetailService.getLoggedInUserDetails().getId().equals(paymentDetail.getUser().getId())){
            throw new InvalidRequestException("You are not authorized to cancel this payment", HttpStatus.UNAUTHORIZED);
        }
        AuctionParticipantResponse participant = auctionParticipantService.getParticipantResponseByItemIdAndUserId(paymentDetail.getItem().getId(), paymentDetail.getUser().getId());
        paymentDetail.setPaymentStatus(PaymentStatus.CANCELLED);
        PaymentDetail save = paymentRepository.save(paymentDetail);
        if (save.getPaymentStatus().equals(PaymentStatus.CANCELLED)){
            auctionParticipantService.setPenalty(save.getItem().getId(), paymentDetail.getUser().getId());
            String penalizedReason = "You have been penalized for cancelling payment";
            MessageTemplate message = MessageTemplate.builder()
                    .name("penalized")
                    .data(Map.of(
                            "reason", penalizedReason,
                            "penalty_amount", participant.getDepositAmount()
                    ))
                    .build();
            notificationService.sendNotification(NotificationChannel.EMAIL, "Penalty", message, save.getUser().getEmail());
        }
        return PaymentMapper.mapToResponse(save);
    }

    @Override
    public GeneralResponse<Map<String, Objects>> completeCallbackPayment(Integer paymentId) {
        PaymentDetail paymentDetail = getPaymentById(paymentId);
        paymentDetail.setPaymentStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(paymentDetail);
        return null;
    }

    @Override
    public GeneralResponse<Map<String, Object>> callback(PaymentCallbackRequest paymentCallbackRequest) {
        return GeneralResponse.<Map<String, Object>>builder()
                .message("Callback successful")
                .data(Map.of(
                        "result", paymentCallbackRequest.getResult(),
                        "paymentCallbackRequest", paymentCallbackRequest
                ))
                .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
                .build();
    }

    @Override
    public List<PaymentResponse> getAllPaymentsResponse() {
        List<PaymentDetail> paymentDetails = getAllPayments();
        return paymentDetails.stream().map(PaymentMapper::mapToResponse).toList();
    }

}
