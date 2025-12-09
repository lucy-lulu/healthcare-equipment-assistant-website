package com.unimelbproject.healthcareequipmentassistant.services;

import com.unimelbproject.healthcareequipmentassistant.models.Product;
import com.unimelbproject.healthcareequipmentassistant.repositories.ProductCategoryRepo;
import com.unimelbproject.healthcareequipmentassistant.repositories.ProductRepo;
import com.unimelbproject.healthcareequipmentassistant.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProductService (no Spring context).
 * Fixes UnnecessaryStubbingException by using lenient class-level strictness
 * and stubbing only what each test needs.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepo productRepository;

    @Mock
    private ProductCategoryRepo productCategoryRepo;

    private Product p1, p2, p3;

    private Product mockProduct() { return Mockito.mock(Product.class); }

    @BeforeEach
    void setUp() {
        p1 = mockProduct();
        p2 = mockProduct();
        p3 = mockProduct();
        // Use lenient type stubs so tests that don't touch these still pass
        lenient().when(p1.getType()).thenReturn("simple");
        lenient().when(p2.getType()).thenReturn("variable");
        lenient().when(p3.getType()).thenReturn("simple");
    }

    @Test
    @DisplayName("getAllProducts(page,size) -> repo.findByTypeIn(List<String>, PageRequest)")
    void getAllProducts_filtersByAllowedTypes() {
        PageRequest pr = PageRequest.of(1, 5);
        Page<Product> page = new PageImpl<>(List.of(p1, p2), pr, 12);

        when(productRepository.findByTypeIn(anyList(), any(PageRequest.class))).thenReturn(page);

        Page<Product> result = productService.getAllProducts(1, 5);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(12);

        ArgumentCaptor<List<String>> typesCap = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<PageRequest> prCap = ArgumentCaptor.forClass(PageRequest.class);
        verify(productRepository).findByTypeIn(typesCap.capture(), prCap.capture());
        assertThat(typesCap.getValue()).isNotEmpty();
        assertThat(prCap.getValue().getPageSize()).isEqualTo(5);
        assertThat(prCap.getValue().getPageNumber()).isEqualTo(1);
    }

    @Test
    @DisplayName("searchProducts(query,null) -> only name search path")
    void searchProducts_nameOnly() {
        when(productRepository.findByNameContainingIgnoreCase("wheel"))
                .thenReturn(List.of(p1));

        List<Product> result = productService.searchProducts("wheel", null);

        assertThat(result).containsExactly(p1);
        verify(productRepository).findByNameContainingIgnoreCase("wheel");
        verifyNoInteractions(productCategoryRepo);
    }

    @Test
    @DisplayName("searchProducts(null,categoryId) -> category-only path uses findAllById")
    void searchProducts_categoryOnly() {
        Long catId = 99L;
        when(productCategoryRepo.findProductIdsByCategoryId(catId))
                .thenReturn(List.of(10L, 20L, 30L));
        when(productRepository.findAllById(List.of(10L, 20L, 30L)))
                .thenReturn(List.of(p2, p3));

        List<Product> result = productService.searchProducts(null, catId);

        assertThat(result).containsExactly(p2, p3);
        verify(productCategoryRepo).findProductIdsByCategoryId(catId);
        verify(productRepository).findAllById(List.of(10L, 20L, 30L));
    }

    @Test
    @DisplayName("searchProducts(query,categoryId) -> combines name + category filters")
    void searchProducts_nameAndCategory() {
        Long catId = 5L;
        when(productCategoryRepo.findProductIdsByCategoryId(catId))
                .thenReturn(List.of(111L, 222L));
        // Be lenient on the IDs list instance/order; verify later with a captor
        when(productRepository.findByIdInAndNameContainingIgnoreCase(anyList(), eq("red")))
                .thenReturn(List.of(p1));

        List<Product> result = productService.searchProducts("red", catId);

        assertThat(result).containsExactly(p1);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Long>> idsCap = ArgumentCaptor.forClass(List.class);
        verify(productRepository).findByIdInAndNameContainingIgnoreCase(idsCap.capture(), eq("red"));
        assertThat(idsCap.getValue()).containsExactlyInAnyOrder(111L, 222L);
    }

    @Test
    @DisplayName("getProductVariations(parentId,cursor,size) -> findByParentIdOrderByNameAsc(Long, Pageable)")
    void getProductVariations_paged() {
        Long parentId = 100L;
        PageRequest pr = PageRequest.of(0, 2);
        Page<Product> page = new PageImpl<>(List.of(p1, p2), pr, 2);

        when(productRepository.findByParentIdOrderByNameAsc(eq(parentId), any(Pageable.class)))
                .thenReturn(page);

        Page<Product> result = productService.getProductVariations(parentId, 0, 2);

        assertThat(result.getContent()).containsExactly(p1, p2);
        verify(productRepository).findByParentIdOrderByNameAsc(eq(parentId), any(Pageable.class));
    }

    @Test
    @DisplayName("getProductVariationsByParentSku(parentSku) -> findByParentSkuOrderByNameAsc")
    void getProductVariationsByParentSku() {
        when(productRepository.findByParentSkuOrderByNameAsc("WHEELCHAIR-001"))
                .thenReturn(List.of(p1, p2, p3));
        List<Product> result = productService.getProductVariationsByParentSku("WHEELCHAIR-001");
        assertThat(result).hasSize(3);
        verify(productRepository).findByParentSkuOrderByNameAsc("WHEELCHAIR-001");
    }

    @Nested
    class GetProductFamily {
        @Test
        @DisplayName("returns empty list when product not found")
        void notFound() {
            when(productRepository.findById(999L)).thenReturn(Optional.empty());
            assertThat(productService.getProductFamily(999L)).isEmpty();
        }

        @Test
        @DisplayName("variation input -> uses its parentId to fetch parent + all variations")
        void variationInput_usesParent() {
            Product variation = mockProduct();
            lenient().when(variation.getType()).thenReturn("simple");
            when(productRepository.findById(200L)).thenReturn(Optional.of(variation));
            when(variation.getParentId()).thenReturn(100L);

            when(productRepository.findByParentIdOrIdOrderByTypeAscNameAsc(100L, 100L))
                    .thenReturn(List.of(p1, p2, p3));

            List<Product> family = productService.getProductFamily(200L);
            assertThat(family).containsExactly(p1, p2, p3);
        }

        @Test
        @DisplayName("parent input -> uses itself as parentId to fetch whole family")
        void parentInput_usesSelf() {
            Product parent = mockProduct();
            lenient().when(parent.getType()).thenReturn("variable");
            when(productRepository.findById(100L)).thenReturn(Optional.of(parent));
            when(parent.getParentId()).thenReturn(null);
            when(productRepository.findByParentIdOrIdOrderByTypeAscNameAsc(100L, 100L))
                    .thenReturn(List.of(p1, p2));

            List<Product> family = productService.getProductFamily(100L);
            assertThat(family).containsExactly(p1, p2);
        }
    }

    @Nested
    class IsVariableProduct {
        @Test
        @DisplayName("isVariableProduct() true when countByParentId > 0")
        void isVariableProduct_true() {
            when(productRepository.countByParentId(123L)).thenReturn(3L);
            assertThat(productService.isVariableProduct(123L)).isTrue();
        }

        @Test
        @DisplayName("isVariableProduct() false when countByParentId == 0")
        void isVariableProduct_false() {
            when(productRepository.countByParentId(123L)).thenReturn(0L);
            assertThat(productService.isVariableProduct(123L)).isFalse();
        }
    }
}



