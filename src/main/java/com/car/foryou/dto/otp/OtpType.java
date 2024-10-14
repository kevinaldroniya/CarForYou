package com.car.foryou.dto.otp;

import lombok.Getter;

@Getter
public enum OtpType {
    REGISTER ("register"),
    LOGIN ("login"),
    FORGOT_PASSWORD ("forgot_password");

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
