package com.car.foryou.service.payment;

import com.car.foryou.dto.GeneralResponse;
import com.car.foryou.dto.payment.*;
import com.car.foryou.exception.InvalidRequestException;
import com.car.foryou.exception.ResourceNotFoundException;
import com.car.foryou.helper.EncryptionHelper;
import com.car.foryou.helper.MidtransHelper;
import com.car.foryou.mapper.PaymentMapper;
import com.car.foryou.model.Participant;
import com.car.foryou.model.Payment;
import com.car.foryou.model.User;
import com.car.foryou.repository.payment.PaymentRepository;
import com.car.foryou.service.participant.ParticipantService;
import com.car.foryou.service.item.ItemService;
import com.car.foryou.service.notification.NotificationService;
import com.car.foryou.service.user.CustomUserDetailService;
import com.car.foryou.service.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRepository paymentRepository;
    private final ParticipantService participantService;
    private final NotificationService notificationService;
    private final MidtransHelper midtransHelper;
    private final EncryptionHelper encryptionHelper;

    private static final String PAYMENT = "Payment";

    public PaymentServiceImpl(PaymentRepository paymentRepository, ParticipantService participantService, NotificationService notificationService, MidtransHelper midtransHelper, EncryptionHelper encryptionHelper) {
        this.paymentRepository = paymentRepository;
        this.participantService = participantService;
        this.notificationService = notificationService;
        this.midtransHelper = midtransHelper;
        this.encryptionHelper = encryptionHelper;
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public Payment getPaymentById(Integer id) {
        return paymentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(PAYMENT, "ID", id)
        );
    }

    @Override
    public PaymentResponse getPaymentResponseById(Integer id) {
        Payment payment = getPaymentById(id);
        return PaymentMapper.mapToResponse(payment);
    }

    @Override
    public GeneralResponse<String> manualPayment(Integer paymentId, PaymentRequest paymentRequest) {
        Payment payment = getPaymentById(paymentId);
        if (!payment.getPaymentStatus().equals(PaymentStatus.PENDING) || payment.getPaymentExpiration().isBefore(Instant.now())){
            throw new InvalidRequestException("Invalid payment", HttpStatus.BAD_REQUEST);
        }
//        if (!payment.getPaymentAmount().equals(paymentRequest.getPaymentAmount())){
//            throw new InvalidRequestException("Payment amount does not match", HttpStatus.BAD_REQUEST);
//        }
//        payment.setPaymentMethod(paymentRequest.getPaymentMethod());
//        payment.setShippingAddress(paymentRequest.getShippingAddress());
//        payment.setShippingProvince(paymentRequest.getShippingProvince());
//        payment.setShippingCity(paymentRequest.getShippingCity());
//        payment.setShippingPostalCode(paymentRequest.getShippingPostalCode());
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);
        return GeneralResponse.<String>builder()
                .message("Payment successful")
                .data(null)
                .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
                .build();
    }


    @Transactional
    @Override
    public GeneralResponse<String> confirmPayment(Integer paymentId) {
//        Payment payment = getPaymentById(paymentId);
//        payment.setPaymentStatus(PaymentStatus.CONFIRMED);
//        paymentRepository.save(payment);
//        itemService.updateItemStatus(payment.getId(), ItemStatus.SOLD);
//        participantService.setWinner(payment.getUser().getId(), payment.getItem().getId());
//        participantService.bulkRefundDeposit(payment.getItem().getId());
//        return GeneralResponse.<String>builder()
//                .message("Payment confirmed")
//                .data(null)
//                .timestamp(ZonedDateTime.now(ZoneId.of("UTC"))).build();
        return null;
    }

    @Override
    public PaymentResponse getPaymentByUserIdAndItemId(Integer userId, Integer itemId) {
//        Payment payment = paymentRepository.findByUserIdAndItemId(userId, itemId).orElseThrow(
//                () -> new ResourceNotFoundException(PAYMENT, "UserId or ItemId", userId+"/"+itemId)
//        );
//        return PaymentMapper.mapToResponse(payment);
        return null;
    }

    @Override
    public PaymentResponse updatePaymentStatus(Integer paymentId, PaymentStatus paymentStatus) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(
                () -> new ResourceNotFoundException(PAYMENT, "ID", paymentId)
        );
        if (paymentStatus.equals(PaymentStatus.CANCELLED) && payment.getPaymentExpiration().isAfter(Instant.now())){
            throw new InvalidRequestException("Payment cannot be cancelled", HttpStatus.BAD_REQUEST);
        }
        payment.setPaymentStatus(paymentStatus);
        Payment save = paymentRepository.save(payment);
        return PaymentMapper.mapToResponse(save);
    }
//
//    @Override
//    public PaymentResponse cancelPayment(PaymentCancelRequest request) {
//        Payment payment = getPaymentById(request.getBidId());
//        if (!payment.getPaymentStatus().equals(PaymentStatus.PENDING)){
//            throw new InvalidRequestException("Payment cannot be cancelled", HttpStatus.BAD_REQUEST);
//        }
//        if (!CustomUserDetailService.getLoggedInUserDetails().getId().equals(payment.getUser().getId())){
//            throw new InvalidRequestException("You are not authorized to cancel this payment", HttpStatus.UNAUTHORIZED);
//        }
//        ParticipantResponse participant = participantService.getParticipantResponseByItemIdAndUserId(payment.getItem().getId(), payment.getUser().getId());
//        payment.setPaymentStatus(PaymentStatus.CANCELLED);
//        Payment save = paymentRepository.save(payment);
//        if (save.getPaymentStatus().equals(PaymentStatus.CANCELLED)){
//            participantService.setPenalty(save.getItem().getId(), payment.getUser().getId());
//            String penalizedReason = "You have been penalized for cancelling payment";
//            MessageTemplate message = MessageTemplate.builder()
//                    .name("penalized")
//                    .data(Map.of(
//                            "reason", penalizedReason,
//                            "penalty_amount", participant.getDepositAmount()
//                    ))
//                    .build();
//            notificationService.sendNotification(NotificationChannel.EMAIL, "Penalty", message, save.getUser().getEmail());
//        }
//        return PaymentMapper.mapToResponse(save);
//    }

    @Override
    public GeneralResponse<Map<String, Objects>> completeCallbackPayment(Integer paymentId) {
        Payment payment = getPaymentById(paymentId);
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);
        return null;
    }

//    @Override
//    public GeneralResponse<Map<String, Object>> callback(PaymentCallbackRequest paymentCallbackRequest) {
//        return GeneralResponse.<Map<String, Object>>builder()
//                .message("Callback successful")
//                .data(Map.of(
//                        "result", paymentCallbackRequest.getResult(),
//                        "paymentCallbackRequest", paymentCallbackRequest
//                ))
//                .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
//                .build();
//    }

    @Override
    public PaymentResponse pay(PaymentRequest request) {
        String paymentType = request.getPaymentType().getValue();
        PaymentMethod paymentMethod = request.getPaymentMethod();
        Participant participant = participantService.getParticipantByIdV2(request.getParticipantId());
        Participant.DepositStatus depositStatus = participant.getDepositStatus();
        String paymentChannel = request.getPaymentChannel();
        switch (paymentType){
            case "deposit":
                if (!depositStatus.equals(Participant.DepositStatus.UNPAID)){
                    throw new InvalidRequestException("This deposit has already paid", HttpStatus.BAD_REQUEST);
                }
                break;
            case "auction":
                break;
            default:
                throw new InvalidRequestException("No payment status found with given value: " + paymentType, HttpStatus.BAD_REQUEST);
        }
        if (paymentMethod.equals(PaymentMethod.CASH)){
            return null;
        }else{
            Integer userId = CustomUserDetailService.getLoggedInUserDetails().getId();
            if (!participant.getUser().getId().equals(userId)){
                throw new InvalidRequestException("You are not authorized to access this data", HttpStatus.UNAUTHORIZED);
            }
        }
        Map<String, Object> paymentRequest = buildPaymentRequest(paymentType, paymentMethod, paymentChannel, participant);
        Map<String, Object> midtransResponse = midtransHelper.callChargeApi(paymentRequest);
        Payment payment = buildPayment(midtransResponse, paymentType, paymentMethod, paymentChannel);
        Payment saved = paymentRepository.save(payment);
        return PaymentMapper.mapToResponse(saved);
    }

    @Override
    public PaymentResponse callbackNotification(Map<String, Object> payload) {
        String orderId = (String) payload.get("order_id");
        Payment payment = getPaymentByOrderId(orderId);
        String signature = (String) payload.get("signature");
        String statusCode = (String) payload.get("status_code");
        String grossAmount = (String) payload.get("gross_amount");
        String serverKey = "SB-Mid-server-Y0QsD9Unzm3njsWg9xsQPAVw";
        String data = orderId+statusCode+grossAmount+serverKey;
        String generateSignatureSHA512 = encryptionHelper.generateSignatureSHA512(data);
        if (!signature.equals(generateSignatureSHA512)){
            throw new InvalidRequestException("Invalid signature", HttpStatus.UNAUTHORIZED);
        }
        String[] splitOrderId = orderId.split("-");
        PaymentType paymentType = PaymentType.fromString(splitOrderId[0]);
        Integer participantId = Integer.valueOf(splitOrderId[1]);
        String transactionStatus =  (String) payload.get("transaction_status");
        PaymentStatus paymentStatus = switch (transactionStatus) {
            case "capture", "settlement" -> PaymentStatus.SUCCESS;
            case "deny", "failure" -> PaymentStatus.FAILED;
            default -> PaymentStatus.fromString(transactionStatus);
        };
        payment.setPaymentStatus(paymentStatus);
        Payment saved = paymentRepository.save(payment);
        if (saved.getPaymentStatus().equals(PaymentStatus.SUCCESS)){
            switch (paymentType.getValue()){
                case "deposit":
                    participantService.updateDepositStatus(participantId, Participant.DepositStatus.PAID);
                    break;
                case "auction":
                    break;
                default:
                    throw new InvalidRequestException("No static const found with given value : " + paymentType.getValue(), HttpStatus.BAD_REQUEST);
            }
        }
        return PaymentMapper.mapToResponse(saved);
    }

    private Payment buildPayment(Map<String, Object> midtransResponse, String paymentType, PaymentMethod paymentMethod, String paymentChannel) {
        String orderId = (String) midtransResponse.get("order_id");
        PaymentType type = PaymentType.fromString(paymentType);
        String paymentStatus = (String) midtransResponse.get("transaction_status");
        String paymentCode;
        switch (paymentChannel){
            case "bca", "bni", "bri", "cimb" :
                List<Map<String,Object>> vaNumbers = (List<Map<String, Object>>) midtransResponse.get("va_numbers");
//                Map<String, Object> va_numbers = (Map<String, Object>) midtransResponse.get("va_numbers");
//                paymentCode = (String) va_numbers.get("va_number");
                Map<String, Object> mapVaNumbers = vaNumbers.get(0);
                paymentCode = (String) mapVaNumbers.get("va_number");
                break;
            case "permata":
                paymentCode = (String) midtransResponse.get("permata_va_number");
                break;
            case "mandiri":
                String billKey = (String) midtransResponse.get("bill_key");
                String billCode = (String) midtransResponse.get("bill_code");
                paymentCode = String.format("%s-%s", billKey, billCode);
                break;
            default:
                throw new InvalidRequestException("No payment channel found with given value : " + paymentChannel, HttpStatus.BAD_REQUEST);
        }

        String stringExpiryTime = (String) midtransResponse.get("expiry_time");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(stringExpiryTime, formatter);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Jakarta"));
        Instant instant = zonedDateTime.toInstant();
        return Payment.builder()
                .orderId(orderId)
                .paymentType(type)
                .paymentAmount((long) Double.parseDouble((String) midtransResponse.get("gross_amount")))
                .paymentStatus(PaymentStatus.fromString(paymentStatus))
                .paymentMethod(paymentMethod.getValue() + "-" + paymentChannel)
                .paymentCode(paymentCode)
                .paymentExpiration(instant)
                .user(User.builder().id(CustomUserDetailService.getLoggedInUserDetails().getId()).build())
                .build();
    }

    private Map<String, Object> buildPaymentRequest(String paymentType, PaymentMethod paymentMethod, String paymentChannel, Participant participant) {
        Map<String, Object> result = new HashMap<>();
        ZonedDateTime orderTime = ZonedDateTime.now(ZoneId.of("Asia/Jakarta"));

        DateTimeFormatter orderIdFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String orderTimeId = orderTime.format(orderIdFormatter);
        Long grossAmount;
        ZonedDateTime expiryTime;
        switch (paymentType){
            case "deposit":
                ZonedDateTime startDate = ZonedDateTime.ofInstant(participant.getAuction().getStartDate(), ZoneId.of("Asia/Jakarta"));
                expiryTime = ZonedDateTime.now(ZoneId.of("Asia/Jakarta")).plusDays(1);
                if (expiryTime.isAfter(startDate)){
                    throw new InvalidRequestException("Registration process has ended", HttpStatus.GONE);
                }
                grossAmount = Long.valueOf(participant.getAuction().getDepositAmount());
                break;
            case "auction":
                expiryTime = ZonedDateTime.ofInstant(participant.getPaymentExpiry(), ZoneId.of("Asia/Jakarta"));
                grossAmount = participant.getHighestBid();
                break;
            default:
                throw new InvalidRequestException("No payment status found with given value: " + paymentType, HttpStatus.BAD_REQUEST);
        }
        result.put("transaction_details", Map.of(
                "order_id", String.format("%s-%d-%s", paymentType, participant.getId(), orderTimeId),
                "gross_amount", grossAmount
        ));

        long minuteDifference = ChronoUnit.MINUTES.between(orderTime, expiryTime);
        if (minuteDifference < 5){
            throw new InvalidRequestException("Payment time has already expired", HttpStatus.GONE);
        }
        DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z");
        String stringOrderTime = orderTime.format(formatter);
        result.put("custom_expiry", Map.of(
                "order_time", stringOrderTime,
                "expiry_duration", minuteDifference
        ));

        switch (paymentChannel){
            case "bca", "bni", "bri", "cimb" :
                result.put("payment_type", "bank_transfer");
                result.put("bank_transfer", Map.of(
                        "bank", paymentChannel
                ));
                break;
            case "permata":
                result.put("payment_type", "permata");
                break;
            case "mandiri":
                result.put("payment_type", "echannel");
                result.put("ecahnnel", Map.of(
                        "bill_info1", "Payment for " + paymentType,
                        "bill_info2", "Online purchase"
                ));
                break;
            default:
                throw new InvalidRequestException("No payment channel found with given value : " + paymentChannel, HttpStatus.BAD_REQUEST);
        }

        Map<String, Object> item = Map.of(
                "id", paymentType + "-" + participant.getId(),
                "price", grossAmount,
                "quantity", 1,
                "name", paymentType
        );
        result.put("item_details", List.of(item));
        result.put("customer_details", Map.of(
                "first_name", participant.getUser().getFirstName(),
                "last_name", participant.getUser().getLastName(),
                "email", participant.getUser().getEmail(),
                "phone", participant.getUser().getPhoneNumber()
        ));
        return result;
    }

    @Override
    public List<PaymentResponse> getAllPaymentsResponse() {
        List<Payment> payments = getAllPayments();
        return payments.stream().map(PaymentMapper::mapToResponse).toList();
    }

    public Payment getPaymentByOrderId(String orderId){
        return paymentRepository.findByOrderId(orderId).orElseThrow(
                () -> new ResourceNotFoundException(PAYMENT, "orderId", orderId)
        );
    }

}
