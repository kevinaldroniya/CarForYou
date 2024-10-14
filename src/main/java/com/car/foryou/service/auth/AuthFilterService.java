package com.car.foryou.service.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
    * This class is responsible for filtering the requests and authenticating the user.
    * It extends the OncePerRequestFilter class.
    * OncePerRequestFilter is a class that provides a method to filter the requests.
    * It is responsible for filtering the requests and authenticating the user using the JWT token.
 */
@Component
public class AuthFilterService extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthFilterService(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /*
        * This method is responsible for filtering the requests and authenticating the user.
        * It extracts the JWT token from the request.
        * It extracts the username from the JWT token.
        * It loads the user details from the userDetailsService object.
        * It checks if the token is valid.
        * It creates an authentication token.
        * It sets the authentication token in the SecurityContextHolder.
        * It filters the request.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Get the request URI
        String requestURI = request.getRequestURI();
        // Extract the JWT token from the request
        String jwt = jwtService.getTokenFromRequest(request);
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }
        // Extract the username from the JWT token
        String username = jwtService.extractUsername(jwt);

        // Load the user details from the userDetailsService object
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Load the user details from the userDetailsService object
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            // Check if the token is valid
            if (jwtService.isTokenValid(jwt, userDetails)) {
                /*
                    * This line of code creates an authentication token
                    * The authentication token is created with the user details, null credentials, and authorities.
                    * null credentials are passed because the user is already authenticated.
                    * authorities are roles assigned to the user
                 */
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                /*
                    * This line of code sets the details of the authentication token
                    * The details of the authentication token are set with the WebAuthenticationDetailsSource object.
                    * The WebAuthenticationDetailsSource object is responsible for building the details of the authentication token.
                 */
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Check if the user is MFA authenticated
               if (!requestURI.equals("/verifyMyEmail") && !jwtService.isMfaAuthenticated(jwt)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                 }

               // Set the authentication token in the SecurityContextHolder
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        /*
            * This line of code filters the request
            * The request is filtered by the filterChain object
            * The filterChain object is responsible for filtering the request
         */
        filterChain.doFilter(request, response);
    }
}
