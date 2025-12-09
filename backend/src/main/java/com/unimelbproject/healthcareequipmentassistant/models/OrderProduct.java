package com.unimelbproject.healthcareequipmentassistant.models;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_product")
public class OrderProduct {

    @EmbeddedId
    private OrderProductId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    // --- Constructors ---
    public OrderProduct() {
        this.id = new OrderProductId(); // Initialize the composite key
    }

    public OrderProduct(Order order, Product product, Integer quantity, BigDecimal price) {
        this();
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.id.setOrderId(order.getId());
        this.id.voidsetProductId(product.getId());
    }

    // --- Getters and Setters ---
    public OrderProductId getId() {
        return id;
    }

    public void setId(OrderProductId id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
        this.id.setOrderId(order.getId()); // Update the ID when setting the order
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        this.id.voidsetProductId(product.getId()); // Update the ID when setting the product
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}