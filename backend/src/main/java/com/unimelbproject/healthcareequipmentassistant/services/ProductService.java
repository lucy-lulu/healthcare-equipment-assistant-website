package com.unimelbproject.healthcareequipmentassistant.services;

import com.unimelbproject.healthcareequipmentassistant.models.Product;
import com.unimelbproject.healthcareequipmentassistant.repositories.ProductCategoryRepo;
import com.unimelbproject.healthcareequipmentassistant.repositories.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepo productRepository;

    @Autowired
    private ProductCategoryRepo productCategoryRepository;


    public Page<Product> getAllProducts(int page, int size) {
        List<String> allowedTypes = List.of("variable", "simple");
        return productRepository.findByTypeIn(allowedTypes, PageRequest.of(page, size));
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }


    /*public List<Product> searchProductsByName(String query) {
        return productRepository.findByNameContainingIgnoreCase(query);
    }*/

    public List<Product> searchProducts(String query, Long categoryId) {
        boolean hasQuery = query != null && !query.trim().isEmpty();
        boolean hasCategory = categoryId != null;

        List<Product> result;

        if (hasQuery && hasCategory) {
            List<Long> productIds = productCategoryRepository.findProductIdsByCategoryId(categoryId);
            if (productIds.isEmpty()) return List.of();
            result = productRepository.findByIdInAndNameContainingIgnoreCase(productIds, query);
        } else if (hasQuery) {
            result = productRepository.findByNameContainingIgnoreCase(query);
        } else if (hasCategory) {
            List<Long> productIds = productCategoryRepository.findProductIdsByCategoryId(categoryId);
            result = productRepository.findAllById(productIds);
        } else {
            result = productRepository.findAll();
        }
        return result.stream()
                .filter(p -> {
                    String type = p.getType();
                    return type != null && (type.equalsIgnoreCase("variable") || type.equalsIgnoreCase("simple"));
                })
                .toList();
    }

    /**
     * Gets all product variations for a given parent product ID with pagination.
     * @param parentId The parent product ID.
     * @param cursor The page number (starting from 0).
     * @param size The number of items per page.
     * @return A page of product variations.
     */
    public Page<Product> getProductVariations(Long parentId, int cursor, int size) {
        return productRepository.findByParentIdOrderByNameAsc(parentId, PageRequest.of(cursor, size));
    }

    /**
     * Gets all product variations for a given parent SKU.
     * @param parentSku The parent product SKU.
     * @return A list of product variations.
     */
    public List<Product> getProductVariationsByParentSku(String parentSku) {
        return productRepository.findByParentSkuOrderByNameAsc(parentSku);
    }

    /**
     * Checks if a product has variations (is a variable product).
     * @param productId The product ID to check.
     * @return True if the product has variations, false otherwise.
     */
    public boolean isVariableProduct(Long productId) {
        return productRepository.countByParentId(productId) > 0;
    }

    /**
     * Gets a parent product and all its variations.
     * @param productId The product ID (can be parent or variation).
     * @return A list containing the parent product and all its variations.
     */
    public List<Product> getProductFamily(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            return List.of();
        }

        Product prod = product.get();
        Long parentId = prod.getParentId() != null ? prod.getParentId() : productId;
        
        return productRepository.findByParentIdOrIdOrderByTypeAscNameAsc(parentId, parentId);
    }
}