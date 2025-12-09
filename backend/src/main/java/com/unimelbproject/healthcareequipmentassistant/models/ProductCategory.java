package com.unimelbproject.healthcareequipmentassistant.models;


import jakarta.persistence.*;

@Entity
@Table(name = "product_category")
@IdClass(ProductCategoryId.class)
public class ProductCategory {

    @Id
    @Column(name = "product_id")
    private Long productId;

    @Id
    @Column(name = "category_id")
    private Long categoryId;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
}
