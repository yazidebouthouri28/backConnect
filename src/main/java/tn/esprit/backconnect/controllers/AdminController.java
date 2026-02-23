package tn.esprit.projetPi.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetPi.dto.*;
import tn.esprit.projetPi.entities.OrderStatus;
import tn.esprit.projetPi.entities.RefundStatus;
import tn.esprit.projetPi.entities.Role;
import tn.esprit.projetPi.services.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final UserProfileService userProfileService;
    private final ProductService productService;
    private final OrderService orderService;
    private final ProductReviewService reviewService;
    private final CouponService couponService;
    private final RefundService refundService;
    private final CategoryService categoryService;
    private final WalletService walletService;

    // ============== DASHBOARD ==============

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardDTO>> getDashboard() {
        AdminDashboardDTO dashboard = adminService.getDashboard();
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemStats() {
        Map<String, Object> stats = adminService.getSystemStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/analytics/revenue")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getRevenueAnalytics(
            @RequestParam(defaultValue = "30") int days) {
        Map<String, BigDecimal> analytics = adminService.getRevenueAnalytics(days);
        return ResponseEntity.ok(ApiResponse.success(analytics));
    }

    @GetMapping("/analytics/orders")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getOrderAnalytics(
            @RequestParam(defaultValue = "30") int days) {
        Map<String, Long> analytics = adminService.getOrderAnalytics(days);
        return ResponseEntity.ok(ApiResponse.success(analytics));
    }

    @GetMapping("/top-sellers")
    public ResponseEntity<ApiResponse<List<SellerStatsDTO>>> getTopSellers(
            @RequestParam(defaultValue = "10") int limit) {
        List<SellerStatsDTO> sellers = adminService.getTopSellers(limit);
        return ResponseEntity.ok(ApiResponse.success(sellers));
    }

    // ============== USER MANAGEMENT ==============

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<PageResponse<UserProfileDTO>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<UserProfileDTO> users = userProfileService.getAllUsers(page, size);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/users/search")
    public ResponseEntity<ApiResponse<PageResponse<UserProfileDTO>>> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<UserProfileDTO> users = userProfileService.searchUsers(query, page, size);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/users/role/{role}")
    public ResponseEntity<ApiResponse<PageResponse<UserProfileDTO>>> getUsersByRole(
            @PathVariable Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<UserProfileDTO> users = userProfileService.getUsersByRole(role, page, size);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/users/sellers")
    public ResponseEntity<ApiResponse<PageResponse<UserProfileDTO>>> getSellers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<UserProfileDTO> users = userProfileService.getSellers(page, size);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserProfileDTO>> getUser(@PathVariable String id) {
        UserProfileDTO user = userProfileService.getProfile(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<ApiResponse<UserProfileDTO>> updateUserStatus(
            @PathVariable String id,
            @RequestBody UpdateUserStatusRequest request) {
        UserProfileDTO user = userProfileService.updateUserStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("User status updated", user));
    }

    @PostMapping("/users/{id}/suspend")
    public ResponseEntity<ApiResponse<UserProfileDTO>> suspendUser(
            @PathVariable String id,
            @RequestParam String reason) {
        UpdateUserStatusRequest request = new UpdateUserStatusRequest();
        request.setIsSuspended(true);
        request.setSuspensionReason(reason);
        UserProfileDTO user = userProfileService.updateUserStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("User suspended", user));
    }

    @PostMapping("/users/{id}/activate")
    public ResponseEntity<ApiResponse<UserProfileDTO>> activateUser(@PathVariable String id) {
        UpdateUserStatusRequest request = new UpdateUserStatusRequest();
        request.setIsActive(true);
        request.setIsSuspended(false);
        UserProfileDTO user = userProfileService.updateUserStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("User activated", user));
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<ApiResponse<UserProfileDTO>> updateUserRole(
            @PathVariable String id,
            @RequestParam Role role) {
        UpdateUserStatusRequest request = new UpdateUserStatusRequest();
        request.setRole(role);
        UserProfileDTO user = userProfileService.updateUserStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("User role updated", user));
    }

    @PostMapping("/users/{id}/verify-seller")
    public ResponseEntity<ApiResponse<UserProfileDTO>> verifySeller(@PathVariable String id) {
        UpdateUserStatusRequest request = new UpdateUserStatusRequest();
        request.setSellerVerified(true);
        UserProfileDTO user = userProfileService.updateUserStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Seller verified", user));
    }

    // ============== PRODUCT MANAGEMENT ==============

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<PageResponse<ProductDTO>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<ProductDTO> products = productService.getAllProducts(page, size);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/products/pending")
    public ResponseEntity<ApiResponse<PageResponse<ProductDTO>>> getPendingProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<ProductDTO> products = productService.getPendingApprovalProducts(page, size);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @PostMapping("/products/{id}/approve")
    public ResponseEntity<ApiResponse<ProductDTO>> approveProduct(@PathVariable String id) {
        ProductDTO product = productService.approveProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product approved", product));
    }

    @PostMapping("/products/{id}/reject")
    public ResponseEntity<ApiResponse<ProductDTO>> rejectProduct(
            @PathVariable String id,
            @RequestParam String reason) {
        ProductDTO product = productService.rejectProduct(id, reason);
        return ResponseEntity.ok(ApiResponse.success("Product rejected", product));
    }

    @PostMapping("/products/{id}/feature")
    public ResponseEntity<ApiResponse<ProductDTO>> toggleFeatured(@PathVariable String id) {
        ProductDTO product = productService.toggleFeatured(id);
        return ResponseEntity.ok(ApiResponse.success("Product featured status toggled", product));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted", null));
    }

    // ============== ORDER MANAGEMENT ==============

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<PageResponse<OrderDTO>>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<OrderDTO> orders = orderService.getAllOrders(page, size);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/orders/status/{status}")
    public ResponseEntity<ApiResponse<PageResponse<OrderDTO>>> getOrdersByStatus(
            @PathVariable OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<OrderDTO> orders = orderService.getOrdersByStatus(status, page, size);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrder(@PathVariable String id) {
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<ApiResponse<OrderDTO>> updateOrderStatus(
            @PathVariable String id,
            @RequestParam OrderStatus status,
            @RequestParam(required = false) String notes,
            @AuthenticationPrincipal UserDetails userDetails) {
        OrderDTO order = orderService.updateOrderStatus(id, status, userDetails.getUsername(), notes);
        return ResponseEntity.ok(ApiResponse.success("Order status updated", order));
    }

    @PutMapping("/orders/{id}/tracking")
    public ResponseEntity<ApiResponse<OrderDTO>> updateTracking(
            @PathVariable String id,
            @RequestParam String trackingNumber,
            @RequestParam(required = false) String carrier) {
        OrderDTO order = orderService.updateTrackingNumber(id, trackingNumber, carrier);
        return ResponseEntity.ok(ApiResponse.success("Tracking updated", order));
    }

    @PostMapping("/orders/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @PathVariable String id,
            @RequestParam String reason) {
        orderService.cancelOrder(id, reason);
        return ResponseEntity.ok(ApiResponse.success("Order cancelled", null));
    }

    // ============== REVIEW MANAGEMENT ==============

    @GetMapping("/reviews/pending")
    public ResponseEntity<ApiResponse<PageResponse<ProductReviewDTO>>> getPendingReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<ProductReviewDTO> reviews = reviewService.getPendingReviews(page, size);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    @PostMapping("/reviews/{id}/approve")
    public ResponseEntity<ApiResponse<ProductReviewDTO>> approveReview(@PathVariable String id) {
        ProductReviewDTO review = reviewService.approveReview(id);
        return ResponseEntity.ok(ApiResponse.success("Review approved", review));
    }

    @PostMapping("/reviews/{id}/reject")
    public ResponseEntity<ApiResponse<ProductReviewDTO>> rejectReview(@PathVariable String id) {
        ProductReviewDTO review = reviewService.rejectReview(id);
        return ResponseEntity.ok(ApiResponse.success("Review rejected", review));
    }

    @PostMapping("/reviews/{id}/feature")
    public ResponseEntity<ApiResponse<ProductReviewDTO>> toggleReviewFeatured(@PathVariable String id) {
        ProductReviewDTO review = reviewService.toggleFeatured(id);
        return ResponseEntity.ok(ApiResponse.success("Review featured status toggled", review));
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {
        reviewService.deleteReview(id, userDetails.getUsername(), true);
        return ResponseEntity.ok(ApiResponse.success("Review deleted", null));
    }

    // ============== COUPON MANAGEMENT ==============

    @GetMapping("/coupons")
    public ResponseEntity<ApiResponse<PageResponse<CouponDTO>>> getAllCoupons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<CouponDTO> coupons = couponService.getAllCoupons(page, size);
        return ResponseEntity.ok(ApiResponse.success(coupons));
    }

    @GetMapping("/coupons/active")
    public ResponseEntity<ApiResponse<PageResponse<CouponDTO>>> getActiveCoupons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<CouponDTO> coupons = couponService.getActiveCoupons(page, size);
        return ResponseEntity.ok(ApiResponse.success(coupons));
    }

    @GetMapping("/coupons/{id}")
    public ResponseEntity<ApiResponse<CouponDTO>> getCoupon(@PathVariable String id) {
        CouponDTO coupon = couponService.getCouponById(id);
        return ResponseEntity.ok(ApiResponse.success(coupon));
    }

    @PostMapping("/coupons")
    public ResponseEntity<ApiResponse<CouponDTO>> createCoupon(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateCouponRequest request) {
        CouponDTO coupon = couponService.createCoupon(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Coupon created", coupon));
    }

    @PutMapping("/coupons/{id}")
    public ResponseEntity<ApiResponse<CouponDTO>> updateCoupon(
            @PathVariable String id,
            @Valid @RequestBody CreateCouponRequest request) {
        CouponDTO coupon = couponService.updateCoupon(id, request);
        return ResponseEntity.ok(ApiResponse.success("Coupon updated", coupon));
    }

    @PostMapping("/coupons/{id}/toggle")
    public ResponseEntity<ApiResponse<CouponDTO>> toggleCouponStatus(@PathVariable String id) {
        CouponDTO coupon = couponService.toggleCouponStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Coupon status toggled", coupon));
    }

    @DeleteMapping("/coupons/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(@PathVariable String id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.ok(ApiResponse.success("Coupon deleted", null));
    }

    // ============== REFUND MANAGEMENT ==============

    @GetMapping("/refunds")
    public ResponseEntity<ApiResponse<PageResponse<RefundRequestDTO>>> getAllRefunds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<RefundRequestDTO> refunds = refundService.getAllRefundRequests(page, size);
        return ResponseEntity.ok(ApiResponse.success(refunds));
    }

    @GetMapping("/refunds/pending")
    public ResponseEntity<ApiResponse<PageResponse<RefundRequestDTO>>> getPendingRefunds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<RefundRequestDTO> refunds = refundService.getRefundRequestsByStatus(
                RefundStatus.PENDING, page, size);
        return ResponseEntity.ok(ApiResponse.success(refunds));
    }

    @GetMapping("/refunds/{id}")
    public ResponseEntity<ApiResponse<RefundRequestDTO>> getRefund(@PathVariable String id) {
        RefundRequestDTO refund = refundService.getRefundRequestById(id);
        return ResponseEntity.ok(ApiResponse.success(refund));
    }

    @PostMapping("/refunds/{id}/approve")
    public ResponseEntity<ApiResponse<RefundRequestDTO>> approveRefund(
            @PathVariable String id,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String notes,
            @AuthenticationPrincipal UserDetails userDetails) {
        RefundRequestDTO refund = refundService.approveRefundRequest(
                id, userDetails.getUsername(), amount, notes);
        return ResponseEntity.ok(ApiResponse.success("Refund approved", refund));
    }

    @PostMapping("/refunds/{id}/reject")
    public ResponseEntity<ApiResponse<RefundRequestDTO>> rejectRefund(
            @PathVariable String id,
            @RequestParam String reason,
            @AuthenticationPrincipal UserDetails userDetails) {
        RefundRequestDTO refund = refundService.rejectRefundRequest(
                id, userDetails.getUsername(), reason);
        return ResponseEntity.ok(ApiResponse.success("Refund rejected", refund));
    }

    @PutMapping("/refunds/{id}/status")
    public ResponseEntity<ApiResponse<RefundRequestDTO>> updateRefundStatus(
            @PathVariable String id,
            @RequestParam RefundStatus status,
            @RequestParam(required = false) String notes) {
        RefundRequestDTO refund = refundService.updateRefundStatus(id, status, notes);
        return ResponseEntity.ok(ApiResponse.success("Refund status updated", refund));
    }

    @PostMapping("/refunds/{id}/items-received")
    public ResponseEntity<ApiResponse<RefundRequestDTO>> markItemsReceived(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {
        RefundRequestDTO refund = refundService.markItemsReceived(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Items marked as received", refund));
    }

    // ============== CATEGORY MANAGEMENT ==============

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<CategoryDTO>> createCategory(
            @Valid @RequestBody CreateCategoryRequest request) {
        CategoryDTO category = categoryService.createCategory(request);
        return ResponseEntity.ok(ApiResponse.success("Category created", category));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<CategoryDTO>> updateCategory(
            @PathVariable String id,
            @RequestBody CreateCategoryRequest request) {
        CategoryDTO category = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success("Category updated", category));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted", null));
    }

    // ============== WALLET & TRANSACTIONS ==============

    @GetMapping("/wallets/{userId}")
    public ResponseEntity<ApiResponse<WalletDTO>> getUserWallet(@PathVariable String userId) {
        WalletDTO wallet = walletService.getWallet(userId);
        return ResponseEntity.ok(ApiResponse.success(wallet));
    }

    @GetMapping("/transactions/{userId}")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getUserTransactions(
            @PathVariable String userId) {
        List<TransactionDTO> transactions = walletService.getTransactions(userId);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }

    @PostMapping("/wallets/{userId}/credit")
    public ResponseEntity<ApiResponse<WalletDTO>> creditWallet(
            @PathVariable String userId,
            @RequestParam BigDecimal amount,
            @RequestParam String description) {
        WalletDTO wallet = walletService.deposit(userId, amount, description);
        return ResponseEntity.ok(ApiResponse.success("Wallet credited", wallet));
    }
}
