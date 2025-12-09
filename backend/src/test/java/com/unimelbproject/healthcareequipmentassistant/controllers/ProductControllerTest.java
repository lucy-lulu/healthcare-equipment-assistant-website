package com.unimelbproject.healthcareequipmentassistant.controllers;

import com.unimelbproject.healthcareequipmentassistant.models.Product;
import com.unimelbproject.healthcareequipmentassistant.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests matching the successful endpoints you provided.
 * No @MockBean; uses Mockito + standalone MockMvc.
 */
@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private Product mockP() { return Mockito.mock(Product.class); }

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    @DisplayName("GET /api/products -> 200 with Page<Product> JSON")
    void getAllProducts_ok() throws Exception {
        Page<Product> page = new PageImpl<>(List.of(mockP(), mockP()), PageRequest.of(0, 2), 2);
        when(productService.getAllProducts(0, 2)).thenReturn(page);

        mockMvc.perform(get("/api/products").param("cursor", "0").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /api/products/search?query=chair&category=1 -> 200 with success body")
    void search_ok() throws Exception {
        when(productService.searchProducts("chair", 1L)).thenReturn(List.of(mockP()));

        mockMvc.perform(get("/api/products/search")
                        .param("query", "chair")
                        .param("category", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("GET /api/products/10/variations -> success when parent exists and page not empty")
    void getVariations_ok() throws Exception {
        when(productService.getProductById(10L)).thenReturn(Optional.of(Mockito.mock(Product.class)));

        Page<Product> page = new PageImpl<>(List.of(Mockito.mock(Product.class)),
                PageRequest.of(0, 2), 1);
        when(productService.getProductVariations(10L, 0, 2)).thenReturn(page);

        mockMvc.perform(get("/api/products/10/variations")
                        .param("cursor", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    @DisplayName("GET /api/products/10/variations -> failure when parent not found")
    void getVariations_parentMissing() throws Exception {
        when(productService.getProductById(10L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/10/variations")
                        .param("cursor", "0").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Parent product not found"));
    }

    @Test
    @DisplayName("GET /api/products/10/variations -> failure when page is empty")
    void getVariations_emptyPage() throws Exception {
        when(productService.getProductById(10L)).thenReturn(Optional.of(Mockito.mock(Product.class)));

        Page<Product> empty = Page.empty(PageRequest.of(0, 2));
        when(productService.getProductVariations(10L, 0, 2)).thenReturn(empty);

        mockMvc.perform(get("/api/products/10/variations")
                        .param("cursor", "0").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("No variations found for this product"));
    }


    @Test
    @DisplayName("GET /api/products/variations/by-parent-sku/C101 -> 200 with success body (IResponse<List<Product>>)")
    void getVariationsByParentSku_ok() throws Exception {
        when(productService.getProductVariationsByParentSku("C101"))
                .thenReturn(List.of(mockP(), mockP()));

        mockMvc.perform(get("/api/products/variations/by-parent-sku")
                        .param("parentSku", "C101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());

    }

    @Test
    @DisplayName("GET /api/products/5/family -> 200 with success body")
    void getFamily_ok() throws Exception {
        when(productService.getProductFamily(5L)).thenReturn(List.of(mockP(), mockP()));

        mockMvc.perform(get("/api/products/5/family"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }
}


