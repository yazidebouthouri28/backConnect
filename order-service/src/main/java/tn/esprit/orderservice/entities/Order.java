package tn.esprit.orderservice.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import tn.esprit.orderservice.enums.OrderStatus;
import tn.esprit.orderservice.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_order_user",   columnList = "user_id"),
        @Index(name = "idx_order_status", columnList = "status"),
        @Index(name = "idx_order_number", columnList = "order_number")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    // ✅ FIX: @Pattern retiré — orderNumber est généré dans @PrePersist, pas par l'utilisateur
    @Column(name = "order_number", unique = true, nullable = false, length = 50)
    private String orderNumber;

    @NotNull(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @NotBlank(message = "User email is required")
    @Email(message = "User email must be a valid email address")
    @Size(max = 255, message = "User email must not exceed 255 characters")
    private String userEmail;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    // ✅ FIX: @Size retiré — la validation de liste ne fonctionne pas bien avec Hibernate
    private List<OrderItem> items = new ArrayList<>();

    // ── Montants ────────────────────────────────────────────────────────────

    @NotNull(message = "Subtotal is required")
    @DecimalMin(value = "0.00", message = "Subtotal must be zero or positive")
    @Column(precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    @NotNull(message = "Discount amount is required")
    @DecimalMin(value = "0.00", message = "Discount amount must be zero or positive")
    @Column(precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @NotNull(message = "Shipping cost is required")
    @DecimalMin(value = "0.00", message = "Shipping cost must be zero or positive")
    @Column(precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal shippingCost = BigDecimal.ZERO;

    @NotNull(message = "Tax amount is required")
    @DecimalMin(value = "0.00", message = "Tax amount must be zero or positive")
    @Column(precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.01", message = "Total amount must be greater than 0")
    @Column(precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    // ── Statuts ─────────────────────────────────────────────────────────────

    @NotNull(message = "Order status is required")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @NotNull(message = "Payment status is required")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    // ── Paiement ────────────────────────────────────────────────────────────

    @Size(max = 50, message = "Payment method must not exceed 50 characters")
    // ✅ FIX: pattern autorise null/vide
    @Pattern(
            regexp = "^$|^[A-Z0-9_]+$",
            message = "Payment method must contain only uppercase letters, digits or underscores"
    )
    private String paymentMethod;

    @Size(max = 255, message = "Payment transaction ID must not exceed 255 characters")
    // ✅ FIX: pattern autorise null/vide
    @Pattern(
            regexp = "^$|^[A-Za-z0-9_\\-]+$",
            message = "Payment transaction ID contains invalid characters"
    )
    private String paymentTransactionId;

    // ── Livraison ───────────────────────────────────────────────────────────

    @NotBlank(message = "Shipping name is required")
    @Size(min = 2, max = 100, message = "Shipping name must be between 2 and 100 characters")
    // ✅ FIX: autorise lettres, espaces, chiffres, tirets, apostrophes
    @Pattern(
            regexp = "^[\\p{L}\\p{N} '\\-]+$",
            message = "Shipping name contains invalid characters"
    )
    private String shippingName;

    @Size(max = 20, message = "Shipping phone must not exceed 20 characters")
    // ✅ FIX: pattern autorise null/vide et formats variés
    @Pattern(
            regexp = "^$|^(\\+?[0-9]{1,3}[\\s\\-]?)?[0-9]{6,14}$",
            message = "Shipping phone is invalid. Example: +216 98 123 456"
    )
    private String shippingPhone;

    @NotBlank(message = "Shipping address is required")
    @Size(min = 5, max = 500, message = "Shipping address must be between 5 and 500 characters")
    @Pattern(
            regexp = "^[^<>{}]*$",
            message = "Shipping address must not contain HTML or special characters"
    )
    @Column(length = 500)
    private String shippingAddress;

    @NotBlank(message = "Shipping city is required")
    @Size(min = 2, max = 100, message = "Shipping city must be between 2 and 100 characters")
    // ✅ FIX: autorise lettres, espaces, chiffres
    @Pattern(
            regexp = "^[\\p{L}\\p{N} '\\-]+$",
            message = "Shipping city contains invalid characters"
    )
    private String shippingCity;

    @NotBlank(message = "Shipping postal code is required")
    @Size(min = 3, max = 20, message = "Shipping postal code must be between 3 and 20 characters")
    // ✅ FIX: pattern simplifié — accepte 1000, 75001, SW1A 1AA
    @Pattern(
            regexp = "^[A-Za-z0-9]{1,10}([\\s\\-][A-Za-z0-9]{1,7})?$",
            message = "Shipping postal code is invalid. Example: 1000 or SW1A 1AA"
    )
    private String shippingPostalCode;

    @NotBlank(message = "Shipping country is required")
    @Size(min = 2, max = 100, message = "Shipping country must be between 2 and 100 characters")
    // ✅ FIX: autorise lettres, espaces, chiffres
    @Pattern(
            regexp = "^[\\p{L}\\p{N} '\\-]+$",
            message = "Shipping country contains invalid characters"
    )
    private String shippingCountry;

    // ── Tracking ────────────────────────────────────────────────────────────

    @Size(max = 100, message = "Tracking number must not exceed 100 characters")
    // ✅ FIX: autorise null/vide
    @Pattern(
            regexp = "^$|^[A-Za-z0-9\\-]+$",
            message = "Tracking number contains invalid characters"
    )
    private String trackingNumber;

    @Size(max = 100, message = "Carrier name must not exceed 100 characters")
    // ✅ FIX: autorise null/vide
    @Pattern(
            regexp = "^$|^[\\p{L}0-9 .,'\\-&]+$",
            message = "Carrier name contains invalid characters"
    )
    private String carrierName;

    // ── Coupon & Notes ──────────────────────────────────────────────────────

    @Size(max = 50, message = "Coupon code must not exceed 50 characters")
    // ✅ FIX: autorise null/vide
    @Pattern(
            regexp = "^$|^[A-Za-z0-9_\\-]+$",
            message = "Coupon code must contain only letters, digits, hyphens or underscores"
    )
    private String couponCode;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    // ✅ FIX: autorise null/vide — @Pattern retiré pour les notes libres
    @Column(length = 1000)
    private String notes;

    // ── Dates ───────────────────────────────────────────────────────────────

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime orderedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime paidAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime shippedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime deliveredAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime cancelledAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // ── Lifecycle ───────────────────────────────────────────────────────────

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        orderedAt = LocalDateTime.now();
        if (orderNumber == null) {
            orderNumber = "ORD-" + System.currentTimeMillis();
        }
        normalizeFields();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        normalizeFields();
    }

    private void normalizeFields() {
        if (userEmail != null)          userEmail          = userEmail.trim().toLowerCase();
        if (shippingName != null)       shippingName       = shippingName.trim();
        if (shippingPhone != null)      shippingPhone      = shippingPhone.trim().replaceAll("\\s+", " ");
        if (shippingAddress != null)    shippingAddress    = shippingAddress.trim();
        if (shippingCity != null)       shippingCity       = shippingCity.trim();
        if (shippingPostalCode != null) shippingPostalCode = shippingPostalCode.trim().toUpperCase();
        if (shippingCountry != null)    shippingCountry    = shippingCountry.trim();
        if (couponCode != null)         couponCode         = couponCode.trim().toUpperCase();
        if (paymentMethod != null)      paymentMethod      = paymentMethod.trim().toUpperCase();
        if (trackingNumber != null)     trackingNumber     = trackingNumber.trim().toUpperCase();
        if (notes != null)              notes              = notes.trim();
    }

    @Transient
    public boolean isTotalConsistent() {
        if (subtotal == null || discountAmount == null
                || shippingCost == null || taxAmount == null || totalAmount == null) return false;
        BigDecimal expected = subtotal
                .subtract(discountAmount)
                .add(shippingCost)
                .add(taxAmount);
        return expected.compareTo(totalAmount) == 0;
    }
}