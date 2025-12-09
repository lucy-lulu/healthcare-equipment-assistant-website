package com.unimelbproject.healthcareequipmentassistant.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JWT authentication response containing token and user information")
public class JwtResponse {
    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Token type", example = "Bearer", defaultValue = "Bearer")
    private String type = "Bearer";

    @Schema(description = "User ID", example = "1")
    private String id;

    @Schema(description = "Username", example = "testuser")
    private String username;

    @Schema(description = "User email", example = "test@example.com")
    private String email;

    @Schema(description = "User role", example = "USER")
    private String role;

    @Schema(description = "User level", example = "1")
    private Integer level;

    public JwtResponse(String accessToken, String id, String username, String email, String role, Integer level) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.level = level;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getLevel() { return level; }

    public void setLevel(Integer level) { this.level = level; }
}
