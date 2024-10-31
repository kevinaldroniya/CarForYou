package com.car.foryou.utils;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class PermittedUrls {
    private static final String BASE_API_V1 = "/api/v1";
    private final Set<String> allowedUrls;

    public PermittedUrls(Set<String> allowedUrls) {
        this.allowedUrls = allowedUrls;
        init();
    }

    private void init() {
        allowedUrls.add(BASE_API_V1 + "/auth/login");
        allowedUrls.add(BASE_API_V1 + "/auth/register");
        allowedUrls.add(BASE_API_V1 + "/notifications/send-fcm");
        allowedUrls.add(BASE_API_V1);
    }

    public boolean isPermitted(String url) {
        return allowedUrls.contains(url);
    }
}
