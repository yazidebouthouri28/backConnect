package tn.esprit.orderservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * CartItem entity - migrated from monolith.
 * Changes:
 * - ID changed from Long to UUID
 * - Removed Product entity reference, replaced with productId (UUID)
 * - Product data is denormalized for display without cross-service calls
 */
@Entity
@Table(name = "cart_items", indexes = {
    @Index(name = "idx_cartitem_cart", columnList = "cart_id"),
    @Index(name = "idx_cartitem_product", columnList = "product_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    /** Product ID from Product Service */
    @Column(name = "product_id")
    private UUID productId;

    /** Denormalized product name for display */
    private String productName;

    /** Denormalized product thumbnail for display */
    private String productThumbnail;

    @Builder.Default
    private Integer quantity = 1;

    @Column(precision = 15, scale = 2)
    private BigDecimal price;

    private String selectedVariant;
    private String selectedColor;
    private String selectedSize;

    private LocalDateTime addedAt;

    @PrePersist
    protected void onCreate() {
        addedAt = LocalDateTime.now();
    }
}
