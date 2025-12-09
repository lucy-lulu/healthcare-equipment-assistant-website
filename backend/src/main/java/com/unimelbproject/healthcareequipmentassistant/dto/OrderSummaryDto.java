package com.unimelbproject.healthcareequipmentassistant.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderSummaryDto {
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime orderDate;

    private BigDecimal totalAmount;
    private String status;
    private String userId;
    private String username;
    private String email;
    private String orderTrackingNumber;
    private String shippingTrackingNumber;
    private String comment;

    // --- Constructor ---
    public OrderSummaryDto(Long id,
                           LocalDateTime orderDate,
                           BigDecimal totalAmount,
                           String status,
                           String userId,
                           String username,
                           String email,
                           String orderTrackingNumber,
                           String shippingTrackingNumber,
                           String comment) {
        this.id = id;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.orderTrackingNumber = orderTrackingNumber;
        this.shippingTrackingNumber = shippingTrackingNumber;
        this.comment = comment;
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getOrderTrackingNumber() { return orderTrackingNumber; }
    public void setOrderTrackingNumber(String orderTrackingNumber) { this.orderTrackingNumber = orderTrackingNumber; }

    public String getShippingTrackingNumber() { return shippingTrackingNumber; }
    public void setShippingTrackingNumber(String shippingTrackingNumber) { this.shippingTrackingNumber = shippingTrackingNumber; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
