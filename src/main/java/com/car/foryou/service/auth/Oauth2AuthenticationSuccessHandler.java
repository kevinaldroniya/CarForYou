package com.car.foryou.service.auth;

import com.car.foryou.model.Group;
import com.car.foryou.model.User;
import com.car.foryou.repository.GroupRepository;
import com.car.foryou.repository.UserRepository;
import com.car.foryou.service.user.CustomUserDetailService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

@Component
@RequiredArgsConstructor
/*
 * This class is responsible for handling the success of the OAuth2 authentication.
 * It generates a token for the user and sends it back to the client.
 * It also checks if the user is already in the database, if not, it creates a new user.
 * it implements the AuthenticationSuccessHandler interface.
 * AuthenticationSuccessHandler is an interface that provides a method to handle the success of the authentication.
 */
public class Oauth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final CustomUserDetailService customUserDetailService;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    /*
        * This method is responsible for handling the success of the OAuth2 authentication.
        * It generates a token for the user and sends it back to the client.
        * It also checks if the user is already in the database, if not, it creates a new user.
     */
    @Transactional
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        /*
            * This line of code gets the user details from the authentication object.
            * The user details are stored in the DefaultOAuth2User object.
            * The email of the user is extracted from the DefaultOAuth2User object.
            * If the email is null, the response status is set to UNAUTHORIZED and the method returns.
            * If the email is not null, the method continues.
         */
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        if (email == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Group group = groupRepository.findByName("USER").orElseThrow(
                () -> new RuntimeException("Groups with given name : 'USER'")
        );
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()){
            String username = email.split("@")[0];
            User userBuild = User.builder()
                    .email(email)
                    .username(username)
                    .password("")
                    .createdAt(Instant.now())
                    .group(group)
                    .build();
            userRepository.save(userBuild);
        }

        /*
            * This line of code loads the user details from the customUserDetailService using the email.
            * The user details are stored in the UserDetails object.
            * The token is generated using the jwtService.
            * The token is sent back to the client in the response.
         */
        UserDetails userDetails = customUserDetailService.loadUserByUsername(email);
        String token = jwtService.generateToken(userDetails, true);

        /*
            * This line of code sets the content type of the response to application/json.
            * It also sets the character encoding of the response to UTF-8.
            * The token is sent back to the client in the response.
         */
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"token\":\"" + token + "\"}");
    }

}
