package com.unimelbproject.healthcareequipmentassistant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for adding a message/comment to an order
 */
public class OrderMessageRequest {

    @NotBlank(message = "Message cannot be blank")
    @Size(max = 1000, message = "Message cannot exceed 1000 characters")
    private String message;

    // Default constructor
    public OrderMessageRequest() {
    }

    // Constructor with message
    public OrderMessageRequest(String message) {
        this.message = message;
    }

    // Getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}