package com.car.foryou.dto.otp;

import lombok.Getter;

@Getter
public enum OtpType {
//    REGISTER ("register"),
    LOGIN ("LOGIN"),
    FORGOT_PASSWORD ("FORGOT_PASSWORD"),
    ENABLED_MFA ("ENABLED_MFA");

    private final String value;

    OtpType(String value) {
        this.value = value;
    }

    public static OtpType fromString(String text) {
        for (OtpType type : OtpType.values()) {
            if (type.value.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}
