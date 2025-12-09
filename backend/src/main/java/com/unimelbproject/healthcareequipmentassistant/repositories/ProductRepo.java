package com.unimelbproject.healthcareequipmentassistant.repositories;

import com.unimelbproject.healthcareequipmentassistant.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepo extends JpaRepository<Product, Long> {

    /**
     * Finds all products whose names contain the given query string, case-insensitive.
     * @param query The search keyword.
     * @return A list of matching products.
     */
    List<Product> findByNameContainingIgnoreCase(String query);

    List<Product> findByIdInAndNameContainingIgnoreCase(List<Long> ids, String query);

    Page<Product> findByTypeIn(List<String> types, PageRequest pageable);


    /**
     * Finds all product variations by parent product ID with pagination.
     * @param parentId The parent product ID.
     * @param pageable Pagination information.
     * @return A page of product variations.
     */
    Page<Product> findByParentIdOrderByNameAsc(Long parentId, Pageable pageable);

    /**
     * Finds all product variations by parent SKU.
     * @param parentSku The parent product SKU.
     * @return A list of product variations.
     */
    List<Product> findByParentSkuOrderByNameAsc(String parentSku);

    /**
     * Finds parent product and all its variations by parent ID.
     * @param parentId The parent product ID.
     * @param id The product ID (for finding the parent).
     * @return A list containing parent and all variations.
     */
    List<Product> findByParentIdOrIdOrderByTypeAscNameAsc(Long parentId, Long id);

    /**
     * Counts the number of variations for a given parent product ID.
     * @param parentId The parent product ID.
     * @return The count of variations.
     */
    long countByParentId(Long parentId);
}
