package com.unimelbproject.healthcareequipmentassistant.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class ManageUserLevelRequest {
    @Email
    private String email;

    @NotNull
    private Integer level;

    // Getters and setters
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getLevel() {
        return level;
    }
    public void setLevel(Integer level) {
        this.level = level;
    }
}
