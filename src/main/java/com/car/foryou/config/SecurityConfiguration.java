package com.car.foryou.config;

import com.car.foryou.service.impl.AuthFilterService;
import com.car.foryou.service.impl.Oauth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfiguration {

    private final AuthFilterService authFilterService;
    private final AuthenticationProvider authenticationProvider;
    private final Oauth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/users/**","/api/v1/items/**","/brands").permitAll()
                        .anyRequest().authenticated())
                /*
                    * This line of code is responsible for setting the session management policy.
                    * The session management policy is set to STATELESS.
                    * STATELESS means that the application will not create a session for the user.
                    * This is because the application is stateless and does not need to store the user's state.
                    * The application will not store the user's state in the session.
                 */
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                /*
                    * This line of code is responsible for setting the authentication provider.
                    * The authentication provider is set to the authenticationProvider object.
                    * The authentication provider is responsible for authenticating the user.
                    * The authentication provider is responsible for checking the user's credentials and authenticating the user.
                    * The authentication provider is responsible for authenticating the user using the authenticationProvider object.
                 */
                .authenticationProvider(authenticationProvider)
                /*
                    * This line of code is responsible for setting the authentication filter.
                    * The authentication filter is set to the authFilterService object.
                    * The authentication filter is responsible for authenticating the user.
                    * The authentication filter is responsible for checking the user's credentials and authenticating the user.
                    * The authentication filter is responsible for authenticating the user using the authFilterService object.
                 */
                .addFilterBefore(authFilterService, UsernamePasswordAuthenticationFilter.class)
                /*
                    * This line of code is responsible for setting the OAuth2 login.
                    * The OAuth2 login is set to the oauth2AuthenticationSuccessHandler object.
                    * The OAuth2 login is responsible for handling the success of the OAuth2 authentication.
                    * The OAuth2 login is responsible for generating a token for the user and sending it back to the client.
                    * The OAuth2 login is responsible for checking if the user is already in the database, if not, it creates a new user.
                    * The OAuth2 login is responsible for handling the success of the OAuth2 authentication using the oauth2AuthenticationSuccessHandler object.
                 */
                .oauth2Login(oauth2 -> oauth2.successHandler(oauth2AuthenticationSuccessHandler));

        return http.build();
    }
}
