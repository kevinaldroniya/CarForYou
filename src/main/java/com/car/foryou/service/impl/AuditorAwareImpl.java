package com.car.foryou.service.impl;

import com.car.foryou.dto.user.UserInfoDetails;
import lombok.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/*
    * This class is responsible for providing the current auditor.
    * implements the AuditorAware interface.
    * AuditorAware is an interface that provides a method to get the current auditor.
 */
public class AuditorAwareImpl implements AuditorAware<Integer> {
    @Override
    @NonNull
    public Optional<Integer> getCurrentAuditor() {
        // Get the current authentication object
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Check if the authentication object is not null and the user is authenticated
        if (authentication != null && authentication.isAuthenticated()){
            // Get the user details from the authentication object
            UserInfoDetails user = (UserInfoDetails) authentication.getPrincipal();
            // Return the user id
            return Optional.of(user.getId());
        }
        return Optional.empty();
    }
}
