package com.car.foryou.model;

import com.car.foryou.model.baseattribute.BaseModel;
import com.car.foryou.service.user.CustomUserDetailService;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@Entity
@Table(name = "refresh_token")
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {
    @Column(name = "token")
    private String token;

    @Column(name = "expiration_time")
    private Instant expirationTime;

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

//    @PrePersist
//    public void onCreate(){
//        this.createdAt = Instant.now();
//        this.createdBy = CustomUserDetailService.getLoggedInUserDetails().getId();
//    }

    @PreUpdate
    public void onUpdate(){
        if (deletedAt == null){
            this.updatedAt = Instant.now();
            this.updatedBy = CustomUserDetailService.getLoggedInUserDetails().getId();
        }else {
            this.deletedAt = Instant.now();
            this.deletedBy = CustomUserDetailService.getLoggedInUserDetails().getId();
        }
    }
}
