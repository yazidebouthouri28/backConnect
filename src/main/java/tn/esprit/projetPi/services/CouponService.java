package tn.esprit.projetPi.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tn.esprit.projetPi.dto.*;
import tn.esprit.projetPi.entities.Coupon;
import tn.esprit.projetPi.entities.CouponUsage;
import tn.esprit.projetPi.entities.DiscountType;
import tn.esprit.projetPi.exception.DuplicateResourceException;
import tn.esprit.projetPi.exception.ResourceNotFoundException;
import tn.esprit.projetPi.repositories.CouponRepository;
import tn.esprit.projetPi.repositories.CouponUsageRepository;
import tn.esprit.projetPi.repositories.OrderRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;
    private final OrderRepository orderRepository;

    public PageResponse<CouponDTO> getAllCoupons(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Coupon> couponPage = couponRepository.findAll(pageable);
        return toPageResponse(couponPage);
    }

    public PageResponse<CouponDTO> getActiveCoupons(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Coupon> couponPage = couponRepository.findByIsActive(true, pageable);
        return toPageResponse(couponPage);
    }

    public List<CouponDTO> getPublicCoupons() {
        return couponRepository.findValidPublicCoupons(LocalDateTime.now()).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CouponDTO getCouponById(String id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));
        return toDTO(coupon);
    }

    public CouponDTO getCouponByCode(String code) {
        Coupon coupon = couponRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with code: " + code));
        return toDTO(coupon);
    }

    public CouponDTO createCoupon(String createdBy, CreateCouponRequest request) {
        if (couponRepository.existsByCodeIgnoreCase(request.getCode())) {
            throw new DuplicateResourceException("Coupon with code '" + request.getCode() + "' already exists");
        }

        Coupon coupon = new Coupon();
        coupon.setCode(request.getCode().toUpperCase());
        coupon.setName(request.getName());
        coupon.setDescription(request.getDescription());
        coupon.setDiscountType(request.getDiscountType());
        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setMinimumOrderAmount(request.getMinimumOrderAmount());
        coupon.setMaximumDiscountAmount(request.getMaximumDiscountAmount());
        coupon.setValidFrom(request.getValidFrom());
        coupon.setValidUntil(request.getValidUntil());
        coupon.setUsageLimit(request.getUsageLimit());
        coupon.setUsageLimitPerUser(request.getUsageLimitPerUser());
        coupon.setUsedCount(0);
        coupon.setIsActive(true);
        coupon.setIsPublic(request.getIsPublic() != null ? request.getIsPublic() : false);
        coupon.setApplicableProductIds(request.getApplicableProductIds());
        coupon.setApplicableCategoryIds(request.getApplicableCategoryIds());
        coupon.setExcludedProductIds(request.getExcludedProductIds());
        coupon.setApplicableUserIds(request.getApplicableUserIds());
        coupon.setFirstOrderOnly(request.getFirstOrderOnly() != null ? request.getFirstOrderOnly() : false);
        coupon.setNewUsersOnly(request.getNewUsersOnly() != null ? request.getNewUsersOnly() : false);
        coupon.setCreatedBy(createdBy);
        coupon.setCreatedAt(LocalDateTime.now());

        Coupon saved = couponRepository.save(coupon);
        log.info("Coupon created: {} by {}", saved.getCode(), createdBy);
        return toDTO(saved);
    }

    public CouponDTO updateCoupon(String id, CreateCouponRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));

        // Check if code is being changed and if new code already exists
        if (!coupon.getCode().equalsIgnoreCase(request.getCode()) &&
            couponRepository.existsByCodeIgnoreCase(request.getCode())) {
            throw new DuplicateResourceException("Coupon with code '" + request.getCode() + "' already exists");
        }

        if (request.getCode() != null) coupon.setCode(request.getCode().toUpperCase());
        if (request.getName() != null) coupon.setName(request.getName());
        if (request.getDescription() != null) coupon.setDescription(request.getDescription());
        if (request.getDiscountType() != null) coupon.setDiscountType(request.getDiscountType());
        if (request.getDiscountValue() != null) coupon.setDiscountValue(request.getDiscountValue());
        if (request.getMinimumOrderAmount() != null) coupon.setMinimumOrderAmount(request.getMinimumOrderAmount());
        if (request.getMaximumDiscountAmount() != null) coupon.setMaximumDiscountAmount(request.getMaximumDiscountAmount());
        if (request.getValidFrom() != null) coupon.setValidFrom(request.getValidFrom());
        if (request.getValidUntil() != null) coupon.setValidUntil(request.getValidUntil());
        if (request.getUsageLimit() != null) coupon.setUsageLimit(request.getUsageLimit());
        if (request.getUsageLimitPerUser() != null) coupon.setUsageLimitPerUser(request.getUsageLimitPerUser());
        if (request.getIsPublic() != null) coupon.setIsPublic(request.getIsPublic());
        coupon.setApplicableProductIds(request.getApplicableProductIds());
        coupon.setApplicableCategoryIds(request.getApplicableCategoryIds());
        coupon.setExcludedProductIds(request.getExcludedProductIds());
        coupon.setApplicableUserIds(request.getApplicableUserIds());
        if (request.getFirstOrderOnly() != null) coupon.setFirstOrderOnly(request.getFirstOrderOnly());
        if (request.getNewUsersOnly() != null) coupon.setNewUsersOnly(request.getNewUsersOnly());
        coupon.setUpdatedAt(LocalDateTime.now());

        return toDTO(couponRepository.save(coupon));
    }

    public CouponDTO toggleCouponStatus(String id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));
        coupon.setIsActive(!Boolean.TRUE.equals(coupon.getIsActive()));
        coupon.setUpdatedAt(LocalDateTime.now());
        return toDTO(couponRepository.save(coupon));
    }

    public void deleteCoupon(String id) {
        if (!couponRepository.existsById(id)) {
            throw new ResourceNotFoundException("Coupon not found with id: " + id);
        }
        couponRepository.deleteById(id);
    }

    public CouponValidationResponse validateAndApplyCoupon(String code, String userId, 
            BigDecimal orderTotal, List<String> productIds) {
        Coupon coupon = couponRepository.findByCodeIgnoreCase(code).orElse(null);

        if (coupon == null) {
            return CouponValidationResponse.builder()
                    .valid(false)
                    .message("Invalid coupon code")
                    .build();
        }

        // Check if active
        if (!Boolean.TRUE.equals(coupon.getIsActive())) {
            return CouponValidationResponse.builder()
                    .valid(false)
                    .message("This coupon is no longer active")
                    .build();
        }

        // Check validity period
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getValidFrom() != null && now.isBefore(coupon.getValidFrom())) {
            return CouponValidationResponse.builder()
                    .valid(false)
                    .message("This coupon is not yet valid")
                    .build();
        }
        if (coupon.getValidUntil() != null && now.isAfter(coupon.getValidUntil())) {
            return CouponValidationResponse.builder()
                    .valid(false)
                    .message("This coupon has expired")
                    .build();
        }

        // Check usage limits
        if (coupon.getUsageLimit() != null && coupon.getUsedCount() >= coupon.getUsageLimit()) {
            return CouponValidationResponse.builder()
                    .valid(false)
                    .message("This coupon has reached its usage limit")
                    .build();
        }

        // Check per-user limit
        if (coupon.getUsageLimitPerUser() != null) {
            long userUsageCount = couponUsageRepository.countByCouponIdAndUserId(coupon.getId(), userId);
            if (userUsageCount >= coupon.getUsageLimitPerUser()) {
                return CouponValidationResponse.builder()
                        .valid(false)
                        .message("You have already used this coupon the maximum number of times")
                        .build();
            }
        }

        // Check minimum order amount
        if (coupon.getMinimumOrderAmount() != null && 
            orderTotal.compareTo(coupon.getMinimumOrderAmount()) < 0) {
            return CouponValidationResponse.builder()
                    .valid(false)
                    .message("Minimum order amount of " + coupon.getMinimumOrderAmount() + " required")
                    .build();
        }

        // Check first order only
        if (Boolean.TRUE.equals(coupon.getFirstOrderOnly())) {
            long orderCount = orderRepository.findByUserId(userId).size();
            if (orderCount > 0) {
                return CouponValidationResponse.builder()
                        .valid(false)
                        .message("This coupon is only valid for first orders")
                        .build();
            }
        }

        // Check user-specific coupons
        if (coupon.getApplicableUserIds() != null && !coupon.getApplicableUserIds().isEmpty() &&
            !coupon.getApplicableUserIds().contains(userId)) {
            return CouponValidationResponse.builder()
                    .valid(false)
                    .message("This coupon is not applicable to your account")
                    .build();
        }

        // Calculate discount
        BigDecimal discount = calculateDiscount(coupon, orderTotal);

        return CouponValidationResponse.builder()
                .valid(true)
                .message("Coupon applied successfully")
                .couponCode(coupon.getCode())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .calculatedDiscount(discount)
                .newTotal(orderTotal.subtract(discount))
                .build();
    }

    public void recordCouponUsage(String couponCode, String userId, String orderId, BigDecimal discountApplied) {
        Coupon coupon = couponRepository.findByCodeIgnoreCase(couponCode)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found: " + couponCode));

        CouponUsage usage = new CouponUsage();
        usage.setCouponId(coupon.getId());
        usage.setCouponCode(coupon.getCode());
        usage.setUserId(userId);
        usage.setOrderId(orderId);
        usage.setDiscountApplied(discountApplied);
        usage.setUsedAt(LocalDateTime.now());
        couponUsageRepository.save(usage);

        // Update coupon usage count
        coupon.setUsedCount((coupon.getUsedCount() != null ? coupon.getUsedCount() : 0) + 1);
        couponRepository.save(coupon);
    }

    private BigDecimal calculateDiscount(Coupon coupon, BigDecimal orderTotal) {
        BigDecimal discount;

        switch (coupon.getDiscountType()) {
            case PERCENTAGE:
                discount = orderTotal.multiply(coupon.getDiscountValue())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                break;
            case FIXED_AMOUNT:
                discount = coupon.getDiscountValue();
                break;
            case FREE_SHIPPING:
                discount = BigDecimal.valueOf(10.00); // Assuming fixed shipping cost
                break;
            default:
                discount = BigDecimal.ZERO;
        }

        // Apply maximum discount limit
        if (coupon.getMaximumDiscountAmount() != null && 
            discount.compareTo(coupon.getMaximumDiscountAmount()) > 0) {
            discount = coupon.getMaximumDiscountAmount();
        }

        // Don't exceed order total
        if (discount.compareTo(orderTotal) > 0) {
            discount = orderTotal;
        }

        return discount;
    }

    private CouponDTO toDTO(Coupon coupon) {
        return CouponDTO.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .name(coupon.getName())
                .description(coupon.getDescription())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .minimumOrderAmount(coupon.getMinimumOrderAmount())
                .maximumDiscountAmount(coupon.getMaximumDiscountAmount())
                .validFrom(coupon.getValidFrom())
                .validUntil(coupon.getValidUntil())
                .usageLimit(coupon.getUsageLimit())
                .usageLimitPerUser(coupon.getUsageLimitPerUser())
                .usedCount(coupon.getUsedCount())
                .isActive(coupon.getIsActive())
                .isPublic(coupon.getIsPublic())
                .applicableProductIds(coupon.getApplicableProductIds())
                .applicableCategoryIds(coupon.getApplicableCategoryIds())
                .firstOrderOnly(coupon.getFirstOrderOnly())
                .newUsersOnly(coupon.getNewUsersOnly())
                .createdAt(coupon.getCreatedAt())
                .build();
    }

    private PageResponse<CouponDTO> toPageResponse(Page<Coupon> page) {
        return PageResponse.<CouponDTO>builder()
                .content(page.getContent().stream().map(this::toDTO).collect(Collectors.toList()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}
