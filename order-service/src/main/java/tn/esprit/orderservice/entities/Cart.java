package tn.esprit.orderservice.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "carts", indexes = {
        @Index(name = "idx_cart_user", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @NotNull(message = "User ID is required")
    @Column(name = "user_id", unique = true, nullable = false)
    private UUID userId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.00", message = "Total amount must be zero or positive")
    @DecimalMax(value = "99999999.99", message = "Total amount must not exceed 99,999,999.99")
    @Digits(integer = 13, fraction = 2, message = "Total: at most 13 integer digits and 2 decimal places")
    @Column(precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @NotNull(message = "Discount amount is required")
    @DecimalMin(value = "0.00", message = "Discount amount must be zero or positive")
    @DecimalMax(value = "99999999.99", message = "Discount amount must not exceed 99,999,999.99")
    @Digits(integer = 13, fraction = 2, message = "Discount: at most 13 integer digits and 2 decimal places")
    @Column(precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    // ex: "SUMMER20", "PROMO10"
    @Size(max = 50, message = "Coupon code must not exceed 50 characters")
    @Pattern(
            regexp = "^[A-Z0-9_\\-]*$",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Coupon code must contain only letters, digits, hyphens or underscores"
    )
    private String appliedCouponCode;

    @JsonIgnore
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @JsonIgnore
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        normalizeFields();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        normalizeFields();
    }

    private void normalizeFields() {
        if (appliedCouponCode != null)
            appliedCouponCode = appliedCouponCode.trim().toUpperCase();
        if (totalAmount == null)    totalAmount    = BigDecimal.ZERO;
        if (discountAmount == null) discountAmount = BigDecimal.ZERO;
    }

    // Recalcule totalAmount à partir des lignes
    public void calculateTotal() {
        this.totalAmount = items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Montant final après remise
    @Transient
    public BigDecimal getFinalAmount() {
        return totalAmount.subtract(discountAmount).max(BigDecimal.ZERO);
    }

    // Vérifie que discountAmount ne dépasse pas totalAmount
    @Transient
    public boolean isDiscountValid() {
        if (discountAmount == null || totalAmount == null) return false;
        return discountAmount.compareTo(totalAmount) <= 0;
    }
}