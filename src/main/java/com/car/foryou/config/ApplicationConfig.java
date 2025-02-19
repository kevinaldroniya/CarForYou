package com.car.foryou.config;

//import com.car.foryou.service.auditoraware.AuditorAwareImpl;
import com.car.foryou.service.group.GroupService;
import com.car.foryou.service.user.CustomUserDetailService;
import com.car.foryou.repository.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
//@EnableJpaAuditing
public class ApplicationConfig {

    private final UserRepository userRepository;
    private final GroupService groupService;

    public ApplicationConfig(UserRepository userRepository, GroupService groupService) {
        this.userRepository = userRepository;
        this.groupService = groupService;
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return new CustomUserDetailService(userRepository, groupService);
    }

    /*
        * This method is responsible for creating an authentication provider.
        * The authentication provider is responsible for authenticating the user.
        * The authentication provider is responsible for checking the user's credentials and authenticating the user.
        * The authentication provider is responsible for authenticating the user using the userDetailsService object.
     */
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public AuditorAware<Integer> auditorProvider(){
//        return new AuditorAwareImpl();
//    }
}

