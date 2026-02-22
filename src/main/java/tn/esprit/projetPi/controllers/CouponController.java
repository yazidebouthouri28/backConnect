package tn.esprit.projetPi.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetPi.dto.*;
import tn.esprit.projetPi.services.CouponService;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<List<CouponDTO>>> getPublicCoupons() {
        List<CouponDTO> coupons = couponService.getPublicCoupons();
        return ResponseEntity.ok(ApiResponse.success(coupons));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CouponDTO>> getCoupon(@PathVariable String id) {
        CouponDTO coupon = couponService.getCouponById(id);
        return ResponseEntity.ok(ApiResponse.success(coupon));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<CouponDTO>> getCouponByCode(@PathVariable String code) {
        CouponDTO coupon = couponService.getCouponByCode(code);
        return ResponseEntity.ok(ApiResponse.success(coupon));
    }

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<CouponValidationResponse>> validateCoupon(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ApplyCouponRequest request) {
        CouponValidationResponse response = couponService.validateAndApplyCoupon(
                request.getCode(),
                userDetails.getUsername(),
                request.getOrderTotal(),
                request.getProductIds());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
