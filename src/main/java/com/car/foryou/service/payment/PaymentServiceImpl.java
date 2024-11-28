package com.car.foryou.service.payment;

import com.car.foryou.dto.GeneralResponse;
import com.car.foryou.dto.auction.AuctionProcessStatus;
import com.car.foryou.dto.payment.*;
import com.car.foryou.exception.InvalidRequestException;
import com.car.foryou.exception.ResourceNotFoundException;
import com.car.foryou.helper.EncryptionHelper;
import com.car.foryou.helper.MidtransHelper;
import com.car.foryou.mapper.PaymentMapper;
import com.car.foryou.model.Auction;
import com.car.foryou.model.Participant;
import com.car.foryou.model.Payment;
import com.car.foryou.model.User;
import com.car.foryou.repository.payment.PaymentRepository;
import com.car.foryou.service.participant.ParticipantService;
import com.car.foryou.service.notification.NotificationService;
import com.car.foryou.service.user.CustomUserDetailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRepository paymentRepository;
    private final ParticipantService participantService;
    private final NotificationService notificationService;
    private final MidtransHelper midtransHelper;
    private final EncryptionHelper encryptionHelper;

    private static final String PAYMENT = "Payment";
    private static final String DEPOSIT = "deposit";
    private static final String AUCTION = "auction";
    private static final String PAYMENT_TYPE = "payment_type";
    private static final String ORDER_ID = "order_id";
    private static final String PERMATA = "permata";
    private static final String ASIA_JAKARTA = "Asia/Jakarta";
    private static final String GROSS_AMOUNT = "gross_amount";
    private static final String BCA = "bca";
    private static final String BNI = "bni";
    private static final String CIMB = "cimb";
    private static final String BRI = "bri";
    private static final String MANDIRI = "mandiri";
    private static final String ECHANNEL = "echannel";
    private static final String BANK_TRANSFER = "bank_transfer";

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
    public List<PaymentResponse> getAllPaymentsResponse() {
        List<Payment> payments = getAllPayments();
        return payments.stream().map(PaymentMapper::mapToResponse).toList();
    }

    public Payment getPaymentByOrderId(String orderId){
        return paymentRepository.findByOrderId(orderId).orElseThrow(
                () -> new ResourceNotFoundException(PAYMENT, "orderId", orderId)
        );
    }

    @Transactional
    @Override
    public GeneralResponse<String> manualPayment(Integer paymentId, PaymentRequest paymentRequest) {
        Payment payment = getPaymentById(paymentId);
        if (!payment.getPaymentStatus().equals(PaymentStatus.PENDING) || payment.getPaymentExpiration().isBefore(Instant.now())){
            throw new InvalidRequestException("Invalid payment", HttpStatus.BAD_REQUEST);
        }
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

    @Override
    public GeneralResponse<Map<String, Objects>> completeCallbackPayment(Integer paymentId) {
        Payment payment = getPaymentById(paymentId);
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);
        return null;
    }


    @Override
    public PaymentResponse payOnline(PaymentRequest request) {
        String paymentType = request.getPaymentType().getValue();
        PaymentMethod paymentMethod = request.getPaymentMethod();
        Participant participant = participantService.getParticipantByIdV2(request.getParticipantId());
        String paymentChannel = request.getPaymentChannel();
        Integer userId = CustomUserDetailService.getLoggedInUserDetails().getId();
        if (!participant.getUser().getId().equals(userId)){
            throw new InvalidRequestException("You are not authorized to access this data", HttpStatus.UNAUTHORIZED);
        }
        Long paymentAmount;
        Instant expiredTime;
        switch (paymentType){
            case DEPOSIT:
                validateDeposit(participant);
                Auction auction = participant.getAuction();
                paymentAmount = Long.valueOf(auction.getDepositAmount());
                expiredTime = auction.getStartDate();
                break;
            case AUCTION:
                validateAuctionPayment(participant);
                paymentAmount = participant.getHighestBid();
                expiredTime = participant.getPaymentExpiry();
                break;
            default:
                throw new InvalidRequestException("No payment status found with given value: " + paymentType, HttpStatus.BAD_REQUEST);
        }

        Map<String, Object> mtPayload = prepareMtPayload(paymentType, paymentChannel, participant, paymentAmount, expiredTime);
        Map<String, Object> midtransResponse = midtransHelper.callChargeApi(mtPayload);
        Payment payment = buildPaymentFromMtResponse(midtransResponse, paymentType, paymentMethod, paymentChannel);
        Payment saved = paymentRepository.save(payment);
        return PaymentMapper.mapToResponse(saved);
    }

    @Transactional
    @Override
    public PaymentResponse callbackNotification(Map<String, Object> payload) {
        String orderId = (String) payload.get(ORDER_ID);
        Payment payment = getPaymentByOrderId(orderId);
        String signature = (String) payload.get("signature_key");
        String statusCode = (String) payload.get("status_code");
        String grossAmount = (String) payload.get(GROSS_AMOUNT);
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
        payment.setPaymentTime(Instant.now());
        Payment saved = paymentRepository.save(payment);
        if (saved.getPaymentStatus().equals(PaymentStatus.SUCCESS)){
            switch (paymentType.getValue()){
                case DEPOSIT:
                    participantService.updateDepositStatus(participantId, Participant.DepositStatus.PAID);
                    break;
                case AUCTION:
                    participantService.updateAuctionProcessStatus(participantId, AuctionProcessStatus.PAYMENT_COMPLETED);
                    break;
                default:
                    throw new InvalidRequestException("No static const found with given value : " + paymentType.getValue(), HttpStatus.BAD_REQUEST);
            }
        }
        return PaymentMapper.mapToResponse(saved);
    }

    @Transactional
    @Override
    public PaymentResponse payOffline(Integer participantId, PaymentType paymentType) {
        Participant participant = participantService.getParticipantByIdV2(participantId);
        Instant paymentExpiration = Instant.now().plus(24, ChronoUnit.HOURS);
        String orderId = generateOrderId(paymentType.getValue(), participantId);
        Long paymentAmount = 0L;
        if (paymentType.equals(PaymentType.DEPOSIT)){
            validateDeposit(participant);
            Integer depositAmount = participant.getAuction().getDepositAmount();
            paymentAmount = Long.valueOf(depositAmount);
            participantService.updateDepositStatus(participantId, Participant.DepositStatus.PAID);
        } else if (paymentType.equals(PaymentType.AUCTION)) {
           validateAuctionPayment(participant);
            paymentAmount = participant.getHighestBid();
            participantService.updateAuctionProcessStatus(participantId, AuctionProcessStatus.PAYMENT_COMPLETED);
        }
        Payment payment = Payment.builder()
                .paymentAmount(paymentAmount)
                .orderId(orderId)
                .paymentTime(Instant.now())
                .paymentExpiration(paymentExpiration)
                .paymentStatus(PaymentStatus.SUCCESS)
                .paymentType(paymentType)
                .user(participant.getUser())
                .build();
        return PaymentMapper.mapToResponse(paymentRepository.save(payment));
    }

    private void validateDeposit(Participant participant){
        Participant.DepositStatus depositStatus = participant.getDepositStatus();
        Instant startDate = participant.getAuction().getStartDate();
        if (!depositStatus.equals(Participant.DepositStatus.UNPAID)){
            throw new InvalidRequestException("This deposit has already paid", HttpStatus.BAD_REQUEST);
        } else if (Instant.now().isAfter(startDate)) {
            throw new InvalidRequestException("Auction has already started", HttpStatus.GONE);
        }
    }

    private void validateAuctionPayment(Participant participant){
        AuctionProcessStatus status = participant.getAuctionProcessStatus();
        Instant paymentExpiry = participant.getPaymentExpiry();
        if (!status.equals(AuctionProcessStatus.PAYMENT_PENDING)){
            throw new InvalidRequestException("You can't make payment for this auction", HttpStatus.BAD_REQUEST);
        } else if (paymentExpiry.isBefore(Instant.now())) {
            throw new InvalidRequestException("Payment time has expired", HttpStatus.GONE);
        }
    }

    private Map<String, Object> prepareMtPayload(String paymentType, String paymentChannel, Participant participant, Long grossAmount, Instant expiredTime) {
        Map<String, Object> result = new HashMap<>();
        ZonedDateTime expiryTime = ZonedDateTime.ofInstant(expiredTime, ZoneId.of(ASIA_JAKARTA));

        result.putAll(buildTransactionDetails(paymentType, participant.getId(), grossAmount));
        result.putAll(buildCustomExpiry(expiryTime));
        result.putAll(buildPaymentChannelDetail(paymentChannel, paymentType));
        result.putAll(buildItemDetails(paymentType, participant.getId(), grossAmount));
        result.putAll(buildCustomerDetail(participant));
        return result;
    }

    private Map<String, Object> buildTransactionDetails(String paymentType, Integer participantId, Long grossAmount){
        String orderId = generateOrderId(paymentType, participantId);
        return Map.of("transaction_details", Map.of(
                ORDER_ID, orderId,
                GROSS_AMOUNT, grossAmount
        ));
    }

    private Map<String, Object> buildCustomExpiry(ZonedDateTime expiredTime){
        ZonedDateTime orderTime = ZonedDateTime.now(ZoneId.of(ASIA_JAKARTA));
        Long minuteDifference = getMinuteDifference(expiredTime, orderTime);
        if (minuteDifference < 5){
            throw new InvalidRequestException("Payment time has already expired", HttpStatus.GONE);
        }
        DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z");
        String stringOrderTime = orderTime.format(formatter);
        return Map.of("custom_expiry", Map.of(
                "order_time", stringOrderTime,
                "expiry_duration", minuteDifference
        ));
    }

    private Map<String, Object> buildPaymentChannelDetail(String paymentChannel, String paymentType){
        Map<String, Object> result = new HashMap<>();
        switch (paymentChannel.toLowerCase()){
            case BCA, BNI, BRI, CIMB -> result.putAll(
                    Map.of(PAYMENT_TYPE, BANK_TRANSFER,
                        "bank", paymentChannel));
            case PERMATA -> result.put(PAYMENT_TYPE, PERMATA);
            case MANDIRI -> result.putAll(
                    Map.of(PAYMENT_TYPE, ECHANNEL,
                    ECHANNEL, Map.of(
                            "bill_info1", "Payment for " + paymentType,
                            "bill_info2", "Online purchase"
                    )
            ));
            default -> throw new InvalidRequestException("No payment channel found with given value : " + paymentChannel, HttpStatus.BAD_REQUEST);
        }
        return result;
    }

    private Map<String, Object> buildItemDetails(String paymentType, Integer participantId, Long grossAmount){
        Map<String, Object> item = Map.of(
                "id", paymentType + "-" + participantId,
                "price", grossAmount,
                "quantity", 1,
                "name", paymentType
        );
        return Map.of("item_details", List.of(item));
    }

    private Map<String, Object> buildCustomerDetail(Participant participant){
        return Map.of("customer_details", Map.of(
                "first_name", participant.getUser().getFirstName(),
                "last_name", participant.getUser().getLastName(),
                "email", participant.getUser().getEmail(),
                "phone", participant.getUser().getPhoneNumber()));
    }

    private String generateOrderId(String paymentType, Integer participantId){
        DateTimeFormatter orderIdFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        ZonedDateTime timeUtc = ZonedDateTime.now(ZoneId.of("UTC"));
        String stringTime = timeUtc.format(orderIdFormatter);
        return String.format("%s-%d-%s", paymentType, participantId, stringTime);
    }

    private Long getMinuteDifference(ZonedDateTime expiredTime, ZonedDateTime orderTime){
        return ChronoUnit.MINUTES.between(orderTime, expiredTime);
    }

    private Payment buildPaymentFromMtResponse(Map<String, Object> response, String paymentType, PaymentMethod paymentMethod, String paymentChannel) {
        String orderId = (String) response.get(ORDER_ID);
        PaymentType type = PaymentType.fromString(paymentType);
        String paymentStatus = (String) response.get("transaction_status");
        String paymentCode = getPaymentCode(paymentChannel, response);
        String stringExpiryTime = (String) response.get("expiry_time");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(stringExpiryTime, formatter);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of(ASIA_JAKARTA));
        Instant instant = zonedDateTime.toInstant();
        return Payment.builder()
                .orderId(orderId)
                .paymentType(type)
                .paymentAmount((long) Double.parseDouble((String) response.get(GROSS_AMOUNT)))
                .paymentStatus(PaymentStatus.fromString(paymentStatus))
                .paymentMethod(paymentMethod.getValue() + "-" + paymentChannel)
                .paymentCode(paymentCode)
                .paymentExpiration(instant)
                .user(User.builder().id(CustomUserDetailService.getLoggedInUserDetails().getId()).build())
                .build();
    }

    private String getPaymentCode(String paymentChannel, Map<String, Object> response) {
        String paymentCode = "";
        switch (paymentChannel.toLowerCase()){
            case BCA, BNI, BRI, CIMB :
                Object vaNumbersObject = response.get("va_numbers");
                if (vaNumbersObject instanceof List<?> vaNumbersList && !vaNumbersList.isEmpty()) {
                    Map<?, ?> firstEntry = (Map<?, ?>) vaNumbersList.get(0);
                    paymentCode = (String) firstEntry.get("va_number");
                }
                break;
            case PERMATA:
                paymentCode = (String) response.get("permata_va_number");
                break;
            case MANDIRI:
                String billKey = (String) response.get("bill_key");
                String billCode = (String) response.get("biller_code");
                paymentCode = String.format("%s-%s", billKey, billCode);
                break;
            default:
                throw new InvalidRequestException("No payment channel found with given value : " + paymentChannel, HttpStatus.BAD_REQUEST);
        }
        return paymentCode;
    }

}
