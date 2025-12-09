package com.unimelbproject.healthcareequipmentassistant.controllers;

import com.unimelbproject.healthcareequipmentassistant.models.Category;
import com.unimelbproject.healthcareequipmentassistant.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Category API", description = "Operations related to product categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Operation(
        summary = "Get all top-level categories", 
        description = "Returns all categories with no parent (i.e., parent_id is null)."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully retrieved categories", 
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Category.class))
            )
        )
    })
    @GetMapping
    public List<Category> getTopLevelCategories() {
        return categoryService.getTopLevelCategories();
    }

    @Operation(summary = "Get all categories", description = "Returns all categories regardless of parent-child structure.")
    @GetMapping("/allcategories")
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @Operation(summary = "Get subcategories by category id", description = "Returns the immediate children of a given category. Returns an empty list if the category is a leaf node.")
    @GetMapping("/{categoryId}")
    public List<Category> getSubcategories(
            @Parameter(description = "ID of the parent category") @PathVariable Long categoryId) {
        return categoryService.getSubcategoriesByParentId(categoryId);
    }
}
