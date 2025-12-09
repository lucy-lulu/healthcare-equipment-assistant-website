package com.unimelbproject.healthcareequipmentassistant.models;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable // Marks this class as embeddable, used for composite primary keys
public class OrderProductId implements Serializable {

    private Long orderId; // Corresponds to 'order_id' in OrderProduct
    private Long productId; // Corresponds to 'product_id' in OrderProduct

    // Constructors
    public OrderProductId() {}

    public OrderProductId(Long orderId, Long productId) {
        this.orderId = orderId;
        this.productId = productId;
    }

    // Getters and Setters
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void voidsetProductId(Long productId) {
        this.productId = productId;
    }

    // IMPORTANT: Equals and hashCode methods are crucial for composite keys
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderProductId that = (OrderProductId) o;
        return Objects.equals(orderId, that.orderId) &&
                Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, productId);
    }
}