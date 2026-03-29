package tn.esprit.projetintegre.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetintegre.dto.ApiResponse;
import tn.esprit.projetintegre.dto.PageResponse;
import tn.esprit.projetintegre.dto.request.SubscriptionRequest;
import tn.esprit.projetintegre.dto.response.SubscriptionResponse;
import tn.esprit.projetintegre.entities.Subscription;
import tn.esprit.projetintegre.entities.User;
import tn.esprit.projetintegre.enums.SubscriptionStatus;
import tn.esprit.projetintegre.mapper.DtoMapper;
import tn.esprit.projetintegre.services.SubscriptionService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions", description = "Subscription management APIs")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final DtoMapper dtoMapper;

    @GetMapping
    @Operation(summary = "Get all subscriptions paginated")
    public ResponseEntity<ApiResponse<PageResponse<SubscriptionResponse>>> getAllSubscriptions(Pageable pageable) {
        Page<Subscription> page = subscriptionService.getAllSubscriptions(pageable);
        Page<SubscriptionResponse> response = page.map(dtoMapper::toSubscriptionResponse);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(response)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get subscription by ID")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> getSubscriptionById(@PathVariable Long id) {
        Subscription subscription = subscriptionService.getSubscriptionById(id);
        return ResponseEntity.ok(ApiResponse.success(dtoMapper.toSubscriptionResponse(subscription)));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get subscriptions by user ID")
    public ResponseEntity<ApiResponse<PageResponse<SubscriptionResponse>>> getSubscriptionsByUserId(
            @PathVariable Long userId, Pageable pageable) {
        Page<Subscription> page = subscriptionService.getSubscriptionsByUserId(userId, pageable);
        Page<SubscriptionResponse> response = page.map(dtoMapper::toSubscriptionResponse);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(response)));
    }

    @GetMapping("/user/{userId}/active")
    @Operation(summary = "Get active subscriptions by user ID")
    public ResponseEntity<ApiResponse<List<SubscriptionResponse>>> getActiveSubscriptionsByUserId(@PathVariable Long userId) {
        List<Subscription> subscriptions = subscriptionService.getActiveSubscriptionsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(dtoMapper.toSubscriptionResponseList(subscriptions)));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get subscriptions by status")
    public ResponseEntity<ApiResponse<List<SubscriptionResponse>>> getSubscriptionsByStatus(@PathVariable SubscriptionStatus status) {
        List<Subscription> subscriptions = subscriptionService.getSubscriptionsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(dtoMapper.toSubscriptionResponseList(subscriptions)));
    }

    @PostMapping
    @Operation(summary = "Create a new subscription")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> createSubscription(
            @Valid @RequestBody SubscriptionRequest request,
            @RequestParam Long userId) {
        Subscription subscription = toEntity(request);
        Subscription created = subscriptionService.createSubscription(subscription, userId);
        return ResponseEntity.ok(ApiResponse.success("Subscription created successfully", dtoMapper.toSubscriptionResponse(created)));
    }

    @PostMapping("/subscribe")
    @Operation(summary = "Subscribe with wallet payment and invoice (legacy flow)")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> subscribe(
            @RequestBody User user,
            @RequestParam String planName,
            @RequestParam BigDecimal price,
            @RequestParam int durationDays) {
        Subscription created = subscriptionService.subscribeWithWalletPayment(user, planName, price, durationDays);
        return ResponseEntity.ok(ApiResponse.success(dtoMapper.toSubscriptionResponse(created)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update subscription")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> updateSubscription(
            @PathVariable Long id,
            @Valid @RequestBody SubscriptionRequest request) {
        Subscription subscriptionDetails = Subscription.builder()
                .planName(request.getPlanName())
                .planType(request.getBillingCycle())
                .price(request.getPrice())
                .autoRenew(request.getAutoRenew())
                .build();

        Subscription updated = subscriptionService.updateSubscription(id, subscriptionDetails);
        return ResponseEntity.ok(ApiResponse.success("Subscription updated successfully",
                dtoMapper.toSubscriptionResponse(updated)));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel subscription")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> cancelSubscription(@PathVariable Long id) {
        Subscription cancelled = subscriptionService.cancelSubscription(id);
        return ResponseEntity.ok(ApiResponse.success("Subscription cancelled successfully", dtoMapper.toSubscriptionResponse(cancelled)));
    }

    @PostMapping("/{id}/renew")
    @Operation(summary = "Renew subscription")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> renewSubscription(@PathVariable Long id) {
        Subscription renewed = subscriptionService.renewSubscription(id, 1);
        return ResponseEntity.ok(ApiResponse.success("Subscription renewed successfully",
                dtoMapper.toSubscriptionResponse(renewed)));
    }

    @PostMapping("/renewals/process")
    @Operation(summary = "Process automatic renewals")
    public ResponseEntity<ApiResponse<Void>> processRenewals() {
        subscriptionService.processSubscriptionRenewals();
        return ResponseEntity.ok(ApiResponse.success("Renewals processed", null));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a subscription")
    public ResponseEntity<ApiResponse<Void>> deleteSubscription(@PathVariable Long id) {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.ok(ApiResponse.success("Subscription deleted successfully", null));
    }

    private Subscription toEntity(SubscriptionRequest request) {
        return Subscription.builder()
                .planName(request.getPlanName())
                .planType(request.getPlanDescription())
                .price(request.getPrice())
                .autoRenew(request.getAutoRenew())
                .build();
    }
}
