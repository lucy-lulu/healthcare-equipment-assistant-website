package com.unimelbproject.healthcareequipmentassistant.repositories;

import com.unimelbproject.healthcareequipmentassistant.models.ProductCategory;
import com.unimelbproject.healthcareequipmentassistant.models.ProductCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface ProductCategoryRepo extends JpaRepository<ProductCategory, ProductCategoryId> {

    @Query("SELECT pc.productId FROM ProductCategory pc WHERE pc.categoryId = :categoryId")
    List<Long> findProductIdsByCategoryId(@Param("categoryId") Long categoryId);
}
