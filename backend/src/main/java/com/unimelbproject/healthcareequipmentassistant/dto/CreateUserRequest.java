package com.unimelbproject.healthcareequipmentassistant.dto;

import com.unimelbproject.healthcareequipmentassistant.models.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Schema(description = "Request payload for creating a new user account")
public class CreateUserRequest {
    
    @Schema(description = "Username for the new user", example = "john_doe", required = true)
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 50, message = "Username must be between 3-50 characters")
    private String username;
    
    @Schema(description = "Email address for the new user", example = "john.doe@example.com", required = true)
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;
    
    @Schema(description = "Password for the new user account", example = "SecurePassword123!", required = true)
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, max = 100, message = "Password must be between 6-100 characters")
    private String password;
    
    @Schema(description = "User role", example = "partner", allowableValues = {"partner", "sales", "ot", "admin"}, required = true)
    @NotNull(message = "Role cannot be null")
    private User.UserRole role;
    
    @Schema(description = "Shipping address for the user", example = "123 Main St, Melbourne VIC 3000")
    private String shippingAddress;
    
    @Schema(description = "Billing address for the user", example = "123 Main St, Melbourne VIC 3000")
    private String billingAddress;

    @Schema(description = "Pricing level (0 for staff; 1-4 for partners). If omitted, defaults to 0 for non-partners and 1 for partners.", example = "1")
    @Min(0) @Max(4)
    private Integer level;

    public CreateUserRequest() {}

    public CreateUserRequest(String username, String email, String password, User.UserRole role, Integer level) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.level = level;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User.UserRole getRole() {
        return role;
    }

    public void setRole(User.UserRole role) {
        this.role = role;
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