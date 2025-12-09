package com.unimelbproject.healthcareequipmentassistant.dto;

import com.unimelbproject.healthcareequipmentassistant.models.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "User information response")
public class UserResponse {
    
    @Schema(description = "Unique user identifier", example = "123e4567-e89b-12d3-a456-426614174000")
    private String id;
    
    @Schema(description = "Username", example = "john_doe")
    private String username;
    
    @Schema(description = "Email address", example = "john.doe@example.com")
    private String email;
    
    @Schema(description = "User role", example = "partner")
    private User.UserRole role;
    
    @Schema(description = "Account creation timestamp", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "Shipping address", example = "123 Main St, Melbourne VIC 3000")
    private String shippingAddress;
    
    @Schema(description = "Billing address", example = "123 Main St, Melbourne VIC 3000")
    private String billingAddress;

    @Schema(description = "Business partner pricing level (0 for NOVIS staff; 1-4 for partners)", example = "1")
    private Integer level;

    public UserResponse() {}

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
        this.shippingAddress = user.getShippingAddress();
        this.billingAddress = user.getBillingAddress();
        this.level = user.getLevel();
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

    public User.UserRole getRole() {
        return role;
    }

    public void setRole(User.UserRole role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public Integer getLevel() { return level; }

    public void setLevel(Integer level) { this.level = level; }
}