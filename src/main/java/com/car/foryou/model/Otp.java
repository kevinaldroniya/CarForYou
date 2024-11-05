package com.car.foryou.model;

import com.car.foryou.dto.otp.OtpType;
import com.car.foryou.model.baseattribute.BaseModel;
import com.car.foryou.service.user.CustomUserDetailService;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "otp")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Otp{

    @Column(name = "otp_number")
    private Integer otpNumber;

    @Column(name = "otp_expiration")
    private Long otpExpiration;

    @Column(name = "otp_type")
    @Enumerated(EnumType.STRING)
    private OtpType otpType;

    @Column(name = "is_used")
    private Boolean isUsed;

    @ManyToOne
    private User user;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @CreatedDate
    @Column(name = "created_at")
    private Instant createdAt;

//    @CreatedBy
//    @Column(name = "created_by")
//    private Integer createdBy;

    @Column(name = "updated_at")
    private Instant updatedAt;

//    @Column(name = "updated_by")
//    private Integer updatedBy;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "deleted_by")
    private Integer deletedBy;

//    @PrePersist
//    public void onCreate(){
//        this.createdAt = Instant.now();
////        this.createdBy = CustomUserDetailService.getLoggedInUserDetails().getId();
//    }

    @PreUpdate
    public void onUpdate(){
        if (deletedAt == null){
            this.updatedAt = Instant.now();
//            this.updatedBy = CustomUserDetailService.getLoggedInUserDetails().getId();
        }else {
            this.deletedAt = Instant.now();
//            this.deletedBy = CustomUserDetailService.getLoggedInUserDetails().getId();
        }
    }

    @PrePersist
    public void onGenerate(){
        this.createdAt = Instant.now();

        isUsed = false;
    }
}
