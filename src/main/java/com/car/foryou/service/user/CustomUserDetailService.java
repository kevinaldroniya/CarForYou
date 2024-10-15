package com.car.foryou.service.user;

import com.car.foryou.dto.user.UserInfoDetails;
import com.car.foryou.model.User;
import com.car.foryou.repository.user.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String emailOrUsernameOrPhoneNumber) throws UsernameNotFoundException {
        User user = userRepository.findByEmailOrUsernameOrPhoneNumber(emailOrUsernameOrPhoneNumber, emailOrUsernameOrPhoneNumber, emailOrUsernameOrPhoneNumber).orElseThrow(
                () -> new UsernameNotFoundException("User Not Found With Username, Email, or Phone Number: " + emailOrUsernameOrPhoneNumber)
        );

        Set<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getGroup().getName()));
        return new UserInfoDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

    public static UserInfoDetails getLoggedInUserDetails(){
        return (UserInfoDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
