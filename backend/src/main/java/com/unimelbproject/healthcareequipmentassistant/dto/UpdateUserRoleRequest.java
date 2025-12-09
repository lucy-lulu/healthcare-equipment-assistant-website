package com.unimelbproject.healthcareequipmentassistant.dto;

import com.unimelbproject.healthcareequipmentassistant.models.User;
import jakarta.validation.constraints.NotNull;

public class UpdateUserRoleRequest {
    
    @NotNull(message = "Role cannot be null")
    private User.UserRole role;

    public UpdateUserRoleRequest() {}

    public UpdateUserRoleRequest(User.UserRole role) {
        this.role = role;
    }

    public User.UserRole getRole() {
        return role;
    }

    public void setRole(User.UserRole role) {
        this.role = role;
    }
}