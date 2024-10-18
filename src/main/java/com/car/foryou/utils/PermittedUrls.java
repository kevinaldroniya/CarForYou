package com.car.foryou.utils;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class PermittedUrls {
    private final Set<String> allowedUrls;

    public PermittedUrls(Set<String> allowedUrls) {
        this.allowedUrls = allowedUrls;
        init();
    }

    private void init() {
        allowedUrls.add("/auth/login");
        allowedUrls.add("/login");
    }

    public boolean isPermitted(String url) {
        return allowedUrls.contains(url);
    }
}
