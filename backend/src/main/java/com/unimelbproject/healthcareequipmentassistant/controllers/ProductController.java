package com.unimelbproject.healthcareequipmentassistant.controllers;


import com.unimelbproject.healthcareequipmentassistant.interfaces.IResponse;
import com.unimelbproject.healthcareequipmentassistant.interfaces.Response;
import com.unimelbproject.healthcareequipmentassistant.models.Product;
import com.unimelbproject.healthcareequipmentassistant.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Parameter;


import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product API", description = "Operations related to products.")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * Retrieve a paginated list of all products with type 'variable' or 'simple'.
     *
     * @param cursor Page number (starting from 0)
     * @param size   Number of products per page
     * @return A page of filtered products
     */
    @Operation(summary = "Get all products (filtered by type, paginated)", description = "Returns a page of products of type 'variable' or 'simple'. Supports pagination.")
    @GetMapping
    public Page<Product> getAllProducts(@RequestParam(defaultValue = "0") int cursor,
                                        @RequestParam(defaultValue = "10") int size){
        return productService.getAllProducts(cursor, size);
    }



    /*
    /**
     * GET endpoint to search products by partial name match.
     * @param query The search keyword for product names.
     * @return A list of products matching the query.
     */
    /* @Operation(
            summary = "Search products by name",
            description = "Performs a partial match search for products by keyword in their name."
    )
    @GetMapping("/search")
    public IResponse<List<Product>> searchProducts(@RequestParam("query") String query) {
        List<Product> products = productService.searchProductsByName(query);
        return Response.success(products);
    }
    */

    /**
     * Search for products by name and/or category.
     *
     * - If both `query` and `category` are provided, returns the intersection of products matching both.
     * - If only one is provided, filters accordingly.
     * - If neither is provided, returns all products.
     *
     * @param query      Optional keyword to match product names (case-insensitive).
     * @param categoryId Optional category ID to filter products.
     * @return A list of matching products.
     */

    @Operation(
            summary = "Search products by keyword and/or category",
            description = "Returns products whose names match the given keyword and/or belong to the specified category. If no parameters are provided, returns all products."
    )
    @GetMapping("/search")
    public IResponse<List<Product>> searchProducts(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "category", required = false) Long categoryId) {

        List<Product> products = productService.searchProducts(query, categoryId);
        return Response.success(products);
    }



    /**
     * GET endpoint to retrieve product variations by parent product ID.
     * @param id The parent product ID.
     * @param cursor Page number (starting from 0).
     * @param size Number of variations per page.
     * @return A paginated list of product variations.
     */
    @Operation(
            summary = "Get product variations by parent ID",
            description = "Returns a paginated list of all variations for a given parent product."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved product variations",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = IResponse.class),
                            examples = @ExampleObject(
                                    name = "Successful variations response",
                                    value = """
                                    {
                                      "success": true,
                                      "message": "Success",
                                      "data": {
                                        "content": [
                                          {
                                            "id": 101,
                                            "parentId": 100,
                                            "parentSku": "WHEELCHAIR-001",
                                            "sku": "WHEELCHAIR-001-RED",
                                            "name": "Manual Wheelchair - Red",
                                            "type": "variation"
                                          }
                                        ],
                                        "totalElements": 5,
                                        "totalPages": 1,
                                        "size": 10,
                                        "number": 0
                                      }
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Parent product not found or has no variations",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = IResponse.class)
                    )
            )
    })
    @GetMapping("/{id}/variations")
    public IResponse<Page<Product>> getProductVariations(
            @Parameter(description = "Parent product ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Page number (starting from 0)")
            @RequestParam(defaultValue = "0") int cursor,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size) {
        
        if (!productService.getProductById(id).isPresent()) {
            return Response.failure("Parent product not found");
        }
        
        Page<Product> variations = productService.getProductVariations(id, cursor, size);
        if (variations.isEmpty()) {
            return Response.failure("No variations found for this product");
        }
        
        return Response.success(variations);
    }

    /**
     * GET endpoint to retrieve product variations by parent SKU.
     * @param parentSku The parent product SKU.
     * @return A list of product variations.
     */
    @Operation(
            summary = "Get product variations by parent SKU",
            description = "Returns all variations for a given parent product SKU."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved product variations",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = IResponse.class),
                            examples = @ExampleObject(
                                    name = "Successful variations by SKU response",
                                    value = """
                                    {
                                      "success": true,
                                      "message": "Success",
                                      "data": [
                                        {
                                          "id": 101,
                                          "parentId": 100,
                                          "parentSku": "WHEELCHAIR-001",
                                          "sku": "WHEELCHAIR-001-RED",
                                          "name": "Manual Wheelchair - Red",
                                          "type": "variation"
                                        },
                                        {
                                          "id": 102,
                                          "parentId": 100,
                                          "parentSku": "WHEELCHAIR-001",
                                          "sku": "WHEELCHAIR-001-BLUE",
                                          "name": "Manual Wheelchair - Blue",
                                          "type": "variation"
                                        }
                                      ]
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid parent SKU or no variations found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = IResponse.class)
                    )
            )
    })
    @GetMapping("/variations/by-parent-sku")
    public IResponse<List<Product>> getVariationsByParentSku(
            @Parameter(description = "Parent product SKU", required = true)
            @RequestParam String parentSku) {
        
        if (parentSku == null || parentSku.trim().isEmpty()) {
            return Response.failure("Parent SKU cannot be empty");
        }
        
        List<Product> variations = productService.getProductVariationsByParentSku(parentSku);
        if (variations.isEmpty()) {
            return Response.failure("No variations found for parent SKU: " + parentSku);
        }
        
        return Response.success(variations);
    }

    /**
     * GET endpoint to retrieve a product family (parent + all variations).
     * @param id The product ID (can be parent or variation).
     * @return A list containing the parent product and all its variations.
     */
    @Operation(
            summary = "Get product family (parent + variations)",
            description = "Returns the parent product and all its variations. Input can be either parent or variation ID."
    )
    @GetMapping("/{id}/family")
    public IResponse<List<Product>> getProductFamily(
            @Parameter(description = "Product ID (parent or variation)", required = true)
            @PathVariable Long id) {
        
        List<Product> family = productService.getProductFamily(id);
        if (family.isEmpty()) {
            return Response.failure("Product not found");
        }
        
        return Response.success(family);
    }

}
