package com.unimelbproject.healthcareequipmentassistant.services;

import com.unimelbproject.healthcareequipmentassistant.models.Response;
import org.springframework.stereotype.Service;
import com.unimelbproject.healthcareequipmentassistant.dto.JwtResponse;
import com.unimelbproject.healthcareequipmentassistant.dto.LoginRequest;
import com.unimelbproject.healthcareequipmentassistant.models.User;
import com.unimelbproject.healthcareequipmentassistant.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class AuthService {

        @Autowired
        private AuthenticationManager authenticationManager;

        @Autowired
        private JwtUtils jwtUtils;

        public Response<JwtResponse> authenticateUser(LoginRequest loginRequest) {
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                                                loginRequest.getPassword()));

                System.out.println("Login request received in controller");
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwt = jwtUtils.generateJwtToken((User) authentication.getPrincipal());

                User userDetails = (User) authentication.getPrincipal();

                return Response.success(
                                "Successfully login",
                                new JwtResponse(
                                                jwt,
                                                userDetails.getId(),
                                                userDetails.getUsername(),
                                                userDetails.getEmail(),
                                                userDetails.getRole().name(),
                                                userDetails.getLevel())
                                                );
        }

        public Response<?> logoutUser() {
                // Clear the current user's SecurityContext
                SecurityContextHolder.clearContext();

                // Note: JWT is stateless, server does not store tokens
                // Actual logout is handled by frontend clearing stored token
                // If server-side blacklist is needed, can add token to Redis blacklist

                return Response.success("Successfully logged out", null);
        }
}
