package com.unimelbproject.healthcareequipmentassistant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Request DTO for updating order status
 */
public class UpdateOrderStatusRequest {

    @NotBlank(message = "Status cannot be blank")
    @Pattern(regexp = "^(?i)(pending|processing|shipped|delivered|cancelled|refunded)$", 
             message = "Status must be one of: pending, processing, shipped, delivered, cancelled, refunded (case insensitive)")
    private String status;

    // Default constructor
    public UpdateOrderStatusRequest() {
    }

    // Constructor with status
    public UpdateOrderStatusRequest(String status) {
        this.status = status;
    }

    // Getters and setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status != null ? status.toLowerCase() : null;
    }
}