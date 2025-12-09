package com.unimelbproject.healthcareequipmentassistant.dto;

import java.math.BigDecimal;

// DTO for individual product items within an order detail
public class ItemInOrderDto {
    private Long productId;
    private String sku;
    private String name;
    private BigDecimal price; // Price at time of order
    private Integer quantity;

    // --- Constructors ---
    public ItemInOrderDto() {}

    public ItemInOrderDto(Long productId, String sku, String name, BigDecimal price, Integer quantity) {
        this.productId = productId;
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    // --- Getters and Setters ---
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}