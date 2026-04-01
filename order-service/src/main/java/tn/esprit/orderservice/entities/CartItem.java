package tn.esprit.orderservice.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cart_items", indexes = {
        @Index(name = "idx_cartitem_cart",    columnList = "cart_id"),
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
    @JsonIgnore
    private Cart cart;

    @NotNull(message = "Product ID is required")
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @NotBlank(message = "Product name is required")
    @Size(min = 1, max = 255, message = "Product name must be between 1 and 255 characters")
    @Pattern(
            regexp = "^[^<>{}]*$",
            message = "Product name must not contain HTML or special characters like < > { }"
    )
    private String productName;

    @Size(max = 500, message = "Product thumbnail URL must not exceed 500 characters")
    @Pattern(
            regexp = "^(https?://[^\\s<>\"{}|\\\\^`]+)?$",
            message = "Product thumbnail must be a valid HTTP/HTTPS URL"
    )
    private String productThumbnail;

    @NotNull(message = "Quantity is required")
    @Min(value = 1,    message = "Quantity must be at least 1")
    @Max(value = 9999, message = "Quantity must not exceed 9999")
    @Builder.Default
    private Integer quantity = 1;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "9999999.99", message = "Price must not exceed 9,999,999.99")
    @Digits(integer = 13, fraction = 2, message = "Price: at most 13 integer digits and 2 decimal places")
    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal price;

    @Size(max = 100, message = "Selected variant must not exceed 100 characters")
    @Pattern(
            regexp = "^[^<>{}]*$",
            message = "Selected variant must not contain HTML or special characters"
    )
    private String selectedVariant;

    @Size(max = 50, message = "Selected color must not exceed 50 characters")
    @Pattern(
            regexp = "^[\\p{L}0-9 '\\-#]*$",
            message = "Selected color contains invalid characters"
    )
    private String selectedColor;

    @Size(max = 20, message = "Selected size must not exceed 20 characters")
    @Pattern(
            regexp = "^[\\p{L}0-9 .\\-/]*$",
            message = "Selected size contains invalid characters"
    )
    private String selectedSize;

    @JsonIgnore
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(updatable = false)
    private LocalDateTime addedAt;

    @PrePersist
    protected void onCreate() {
        addedAt = LocalDateTime.now();
        normalizeFields();
    }

    private void normalizeFields() {
        if (productName != null)    productName    = productName.trim();
        if (selectedVariant != null) selectedVariant = selectedVariant.trim();
        if (selectedColor != null)  selectedColor  = selectedColor.trim();
        if (selectedSize != null)   selectedSize   = selectedSize.trim().toUpperCase();
    }

    // Prix total de la ligne = price × quantity
    @Transient
    public BigDecimal getLineTotal() {
        if (price == null || quantity == null) return BigDecimal.ZERO;
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}