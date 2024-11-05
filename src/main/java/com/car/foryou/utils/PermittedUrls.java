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
        allowedUrls.add("/auth/login");
        allowedUrls.add("/auth/register");
        allowedUrls.add("/notifications/send-fcm");
        allowedUrls.add("/auth/items");
        allowedUrls.add("/auth/items/{itemId}");
    }

    public boolean isPermitted(String url) {
        return allowedUrls.contains(url);
    }
}
