package tn.esprit.productservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.productservice.dto.ApiResponse;
import tn.esprit.productservice.dto.request.CategoryRequest;
import tn.esprit.productservice.dto.response.CategoryResponse;
import tn.esprit.productservice.entities.Category;
import tn.esprit.productservice.mapper.ProductMapper;
import tn.esprit.productservice.services.CategoryService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category management endpoints")
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductMapper mapper;

    @GetMapping
    @Operation(summary = "Get all categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(mapper.toCategoryResponseList(categories)));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getActiveCategories() {
        List<Category> categories = categoryService.getActiveCategories();
        return ResponseEntity.ok(ApiResponse.success(mapper.toCategoryResponseList(categories)));
    }

    @GetMapping("/root")
    @Operation(summary = "Get root categories (no parent)")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getRootCategories() {
        List<Category> categories = categoryService.getRootCategories();
        return ResponseEntity.ok(ApiResponse.success(mapper.toCategoryResponseList(categories)));
    }

    @GetMapping("/{id}/subcategories")
    @Operation(summary = "Get subcategories of a category")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getSubcategories(@PathVariable UUID id) {
        List<Category> categories = categoryService.getSubcategories(id);
        return ResponseEntity.ok(ApiResponse.success(mapper.toCategoryResponseList(categories)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable UUID id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success(mapper.toCategoryResponse(category)));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get category by slug")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryBySlug(@PathVariable String slug) {
        Category category = categoryService.getCategoryBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(mapper.toCategoryResponse(category)));
    }

    @PostMapping
    @Operation(summary = "Create a new category")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryRequest request) {
        Category category = mapToCategory(request);
        Category created = categoryService.createCategory(category, request.getParentId());
        return ResponseEntity.ok(ApiResponse.success("Category created successfully", mapper.toCategoryResponse(created)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a category")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryRequest request) {
        Category updated = categoryService.updateCategory(id, mapToCategory(request));
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", mapper.toCategoryResponse(updated)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted", null));
    }

    private Category mapToCategory(CategoryRequest request) {
        return Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .image(request.getImage())
                .slug(request.getSlug())
                .displayOrder(request.getDisplayOrder())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
    }
}