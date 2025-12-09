package com.unimelbproject.healthcareequipmentassistant.models;


import java.io.Serializable;
import java.util.Objects;

public class ProductCategoryId implements Serializable {
    private Long productId;
    private Long categoryId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductCategoryId)) return false;
        ProductCategoryId that = (ProductCategoryId) o;
        return Objects.equals(productId, that.productId) &&
                Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, categoryId);
    }
}
