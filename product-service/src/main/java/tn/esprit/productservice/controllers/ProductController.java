package tn.esprit.productservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.productservice.dto.ApiResponse;
import tn.esprit.productservice.dto.PageResponse;
import tn.esprit.productservice.dto.request.ProductRequest;
import tn.esprit.productservice.dto.response.ProductResponse;
import tn.esprit.productservice.entities.Product;
import tn.esprit.productservice.mapper.ProductMapper;
import tn.esprit.productservice.services.ProductService;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * Product REST Controller
 *
 * KEY FIXES:
 * - getProductsByCategory / getProductsBySeller / searchProducts /
 *   getProductsByPriceRange: service now returns Page<ProductResponse> directly.
 *   Controller no longer calls mapper on raw entities — the dangerous
 *   `products.map(mapper::toProductResponse)` lines are gone.
 *
 * - getFeaturedProducts / getTopSellingProducts: service now returns
 *   List<ProductResponse> directly.
 *
 * - getProductById: still calls mapper once on the entity returned by the
 *   service, which now uses findByIdWithCategory() so category is loaded.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product management endpoints")
public class ProductController {

    private final ProductService productService;
    private final ProductMapper mapper;

    @GetMapping
    @Operation(summary = "Get all active products with pagination")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        List<ProductResponse> products = productService.getActiveProducts(pageable);
        Page<ProductResponse> pageResult = new PageImpl<>(products, pageable, products.size());
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(pageResult)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable UUID id) {
        productService.incrementViewCount(id);
        // getProductById now uses JOIN FETCH so category is always loaded
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success(mapper.toProductResponse(product)));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get products by category")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getProductsByCategory(
            @PathVariable UUID categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // FIX: service returns Page<ProductResponse> — no mapper call needed here
        Page<ProductResponse> response = productService.getProductsByCategory(categoryId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(response)));
    }

    @GetMapping("/seller/{sellerId}")
    @Operation(summary = "Get products by seller")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getProductsBySeller(
            @PathVariable UUID sellerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // FIX: service returns Page<ProductResponse>
        Page<ProductResponse> response = productService.getProductsBySeller(sellerId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(response)));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // FIX: service returns Page<ProductResponse>
        Page<ProductResponse> response = productService.searchProducts(keyword, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(response)));
    }

    @GetMapping("/price-range")
    @Operation(summary = "Get products by price range")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getProductsByPriceRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // FIX: service returns Page<ProductResponse>
        Page<ProductResponse> response = productService.getProductsByPriceRange(min, max, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(response)));
    }

    @GetMapping("/featured")
    @Operation(summary = "Get featured products")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getFeaturedProducts() {
        // FIX: service returns List<ProductResponse>
        return ResponseEntity.ok(ApiResponse.success(productService.getFeaturedProducts()));
    }

    @GetMapping("/top-selling")
    @Operation(summary = "Get top selling products")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getTopSellingProducts(
            @RequestParam(defaultValue = "10") int limit) {
        // FIX: service returns List<ProductResponse>
        return ResponseEntity.ok(ApiResponse.success(productService.getTopSellingProducts(limit)));
    }

    @PostMapping
    @Operation(summary = "Create a new product")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        UUID sellerId = request.getSellerId();
        if (sellerId == null && userIdHeader != null) {
            sellerId = UUID.fromString(userIdHeader);
        }
        Product product = mapToProduct(request);
        Product created = productService.createProduct(product, request.getCategoryId(), sellerId);
        return ResponseEntity.ok(ApiResponse.success("Product created successfully", mapper.toProductResponse(created)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody ProductRequest request) {
        Product updated = productService.updateProduct(id, mapToProduct(request));
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", mapper.toProductResponse(updated)));
    }

    @PatchMapping("/{id}/stock")
    @Operation(summary = "Update product stock")
    public ResponseEntity<ApiResponse<ProductResponse>> updateStock(
            @PathVariable UUID id,
            @RequestParam int quantity) {
        Product updated = productService.updateStock(id, quantity);
        return ResponseEntity.ok(ApiResponse.success("Stock updated successfully", mapper.toProductResponse(updated)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted", null));
    }

    // ── Private helper ──────────────────────────────────────────────────────

    private Product mapToProduct(ProductRequest request) {
        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .originalPrice(request.getOriginalPrice())
                .discountPercentage(request.getDiscountPercentage())
                .sku(request.getSku())
                .barcode(request.getBarcode())
                .brand(request.getBrand())
                .stockQuantity(request.getStockQuantity())
                .minStockLevel(request.getMinStockLevel())
                .maxStockLevel(request.getMaxStockLevel())
                .trackInventory(request.getTrackInventory())
                .images(request.getImages() != null ? new HashSet<>(request.getImages()) : new HashSet<>())
                .tags(request.getTags() != null ? new HashSet<>(request.getTags()) : new HashSet<>())                .thumbnail(request.getThumbnail())
                .isFeatured(request.getIsFeatured())
                .isOnSale(request.getIsOnSale())
                .isRentable(request.getIsRentable())
                .rentalPricePerDay(request.getRentalPricePerDay())
                .weight(request.getWeight())
                .dimensions(request.getDimensions())
                .build();
    }
}