package com.car.foryou.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class User extends ModelTemplate{

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

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @OneToOne(mappedBy = "user")
    private Otp otp;

    @OneToOne(mappedBy = "user")
    private RefreshToken refreshToken;


}
