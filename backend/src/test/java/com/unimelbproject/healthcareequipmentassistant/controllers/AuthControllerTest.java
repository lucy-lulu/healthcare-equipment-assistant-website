package com.unimelbproject.healthcareequipmentassistant.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimelbproject.healthcareequipmentassistant.dto.LoginRequest;
import com.unimelbproject.healthcareequipmentassistant.models.User;
import com.unimelbproject.healthcareequipmentassistant.repositories.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // clear all users
        userRepo.deleteAll();
        User user = new User();
        user.setUsername("testuser");
        user.setPasswordHash(passwordEncoder.encode("testpassword"));
        user.setEmail("testuser@example.com");
        user.setRole(User.UserRole.sales); 
        userRepo.save(user);
    }

    @Test
    @DisplayName("Login API should return 200 for valid credentials")
    void testLoginSuccess() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("testpassword");

        ResultActions result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        result.andExpect(status().isOk());
        }

    @Test
    @DisplayName("Logout API should return 200")
    void testLogout() throws Exception {
        ResultActions result = mockMvc.perform(post("/api/auth/logout"));
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Successfully logged out"));
    }
}