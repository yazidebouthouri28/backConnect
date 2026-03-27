package tn.esprit.orderservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * OrderItem entity - migrated from monolith.
 * Changes:
 * - ID changed from Long to UUID
 * - Removed Product entity reference, replaced with productId (UUID)
 * - Product data is denormalized (snapshot) at order time for data consistency
 */
@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    /** Product ID from Product Service - denormalized reference */
    @Column(name = "product_id")
    private UUID productId;

    /** Snapshot of product name at order time */
    private String productName;

    /** Snapshot of product SKU at order time */
    private String productSku;

    /** Snapshot of product thumbnail at order time */
    private String productThumbnail;

    private Integer quantity;

    @Column(precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalPrice;

    private String selectedVariant;
    private String selectedColor;
    private String selectedSize;
}
