package com.unimelbproject.healthcareequipmentassistant.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class PlaceOrderRequest {

    @NotEmpty(message = "Order items cannot be empty")
    private List<OrderItemPayload> items;

    // Optional comment from user
    private String comment;

    public List<OrderItemPayload> getItems() {
        return items;
    }

    public void setItems(List<OrderItemPayload> items) {
        this.items = items;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    // payload for each line item
    public static class OrderItemPayload {
        @NotNull(message = "productId is required")
        private Long productId;

        @NotNull
        @Min(value = 1, message = "quantity must be >= 1")
        private Integer quantity;

        public OrderItemPayload() {}

        public OrderItemPayload(Long productId, Integer quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
