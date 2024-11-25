package com.car.foryou.model;

import com.car.foryou.service.user.CustomUserDetailService;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User{
    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "is_verified")
    private boolean isVerified;

    @Column(name = "is_oauth")
    private boolean isOauth;

    @Column(name = "is_mfa_enabled")
    private boolean isMfaEnabled;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @CreatedDate
    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "deleted_by")
    private Integer deletedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

//    @OneToMany(mappedBy = "user")
//    private List<Otp> otp;


//    @OneToMany(mappedBy = "user")
//    private List<Participant> participants;

//    @OneToMany(mappedBy = "auctioneer")
//    private List<Item> items;

//    @OneToMany(mappedBy = "user")
//    private List<Bid> bids;

//    @OneToMany(mappedBy = "user")
//    private List<Payment> payments;

    @PrePersist
    public void onCreate(){
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void onUpdate(){
        if (deletedAt == null){
            updatedAt = Instant.now();
            try {
                updatedBy = CustomUserDetailService.getLoggedInUserDetails().getId();
            }catch (Exception e){
                updatedBy = id;
            }
        }else {
            deletedAt = Instant.now();
            deletedBy = CustomUserDetailService.getLoggedInUserDetails().getId();
        }
    }
}
