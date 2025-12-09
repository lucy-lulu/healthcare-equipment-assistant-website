package com.unimelbproject.healthcareequipmentassistant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "User login request containing username and password")
public class LoginRequest {
    @Schema(description = "Username for authentication", example = "testuser", required = true)
    @NotBlank
    private String username;

    @Schema(description = "Password for authentication", example = "password123", required = true)
    @NotBlank
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
