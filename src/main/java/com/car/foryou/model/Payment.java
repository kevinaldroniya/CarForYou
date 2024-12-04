package com.car.foryou.model;

import com.car.foryou.dto.payment.PaymentMethod;
import com.car.foryou.dto.payment.PaymentStatus;
import com.car.foryou.dto.payment.PaymentType;
import com.car.foryou.model.baseattribute.BaseModel;
import com.car.foryou.service.user.CustomUserDetailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Entity
@Table(name = "payment")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Payment {
    @Column(name = "payment_amount")
    private Long paymentAmount;

    @Column(name = "payment_time")
    private Instant paymentTime;

    @Column(name = "payment_proof")
    private String paymentProof;

    @Column(name = "payment_expiration")
    private Instant paymentExpiration;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "order_id")
    private String orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    private PaymentType paymentType;

    @Column(name = "payment_code")
    private String paymentCode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @CreatedDate
    @Column(name = "created_at")
    private Instant createdAt;

    @CreatedBy
    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "deleted_by")
    private Integer deletedBy;

    @Column(name = "log", columnDefinition = "json")
    private String log;

    @PrePersist
    public void onCreate(){
        this.createdAt = Instant.now();
        this.createdBy = CustomUserDetailService.getLoggedInUserDetails().getId();
    }

    @PreUpdate
    public void onUpdate(){
        System.out.println("onUpdate method called");
        Object userDetails = CustomUserDetailService.getLoggedInUserDetails();
        if (deletedAt == null){
            this.updatedAt = Instant.now();
            if (Objects.nonNull(CustomUserDetailService.getLoggedInUserDetails())){
                this.updatedBy = CustomUserDetailService.getLoggedInUserDetails().getId();
            }else {
                this.updatedBy = createdBy;
            }
        }else {
            this.deletedAt = Instant.now();
            this.deletedBy = CustomUserDetailService.getLoggedInUserDetails().getId();
        }
        ObjectMapper objectMapper = new ObjectMapper();
    }

    @Override
    public String toString() {
        return "{\n" +
                "  \"id\": " + id + ",\n" +
                "  \"paymentAmount\": " + paymentAmount + ",\n" +
                "  \"paymentTime\": \"" + paymentTime + "\",\n" +
                "  \"paymentProof\": \"" + paymentProof + "\",\n" +
                "  \"paymentExpiration\": \"" + paymentExpiration + "\",\n" +
                "  \"paymentStatus\": \"" + paymentStatus + "\",\n" +
                "  \"paymentMethod\": \"" + paymentMethod + "\",\n" +
                "  \"orderId\": \"" + orderId + "\",\n" +
                "  \"paymentType\": \"" + paymentType + "\",\n" +
                "  \"paymentCode\": \"" + paymentCode + "\",\n" +
                "  \"userId\": \"" +(user != null ? user.getId() : "null") + "\",\n" +
                "  \"createdAt\": \"" + createdAt + "\",\n" +
                "  \"createdBy\": " + createdBy + ",\n" +
                "  \"updatedAt\": \"" + updatedAt + "\",\n" +
                "  \"updatedBy\": " + updatedBy + ",\n" +
                "  \"deletedAt\": \"" + deletedAt + "\",\n" +
                "  \"deletedBy\": " + deletedBy + "\n" +
                "}";
    }

}
