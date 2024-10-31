package com.car.foryou.dto.payment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BankAccount {
    private String bankName;
    private String accountType;
    private String accountNumber;
    private String accountHolder;
    private String brand;
}
