package tn.esprit.orderservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

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
    @JsonIgnore
    private Order order;

    @NotNull(message = "Product ID is required")
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    // Nom du produit — lettres, chiffres, ponctuation courante
    @NotBlank(message = "Product name is required")
    @Size(min = 1, max = 255, message = "Product name must be between 1 and 255 characters")
    @Pattern(
            regexp = "^[^<>{}]*$",
            message = "Product name must not contain HTML or special characters like < > { }"
    )
    private String productName;

    // SKU — alphanumérique + tirets/underscores (ex: SHOE-RED-42, ABC_123)
    @Size(max = 100, message = "Product SKU must not exceed 100 characters")
    @Pattern(
            regexp = "^[A-Z0-9_\\-]*$",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Product SKU must contain only letters, digits, hyphens or underscores"
    )
    private String productSku;

    // URL thumbnail — http/https uniquement
    @Size(max = 500, message = "Product thumbnail URL must not exceed 500 characters")
    @Pattern(
            regexp = "^(https?://[^\\s<>\"{}|\\\\^`]+)?$",
            message = "Product thumbnail must be a valid HTTP/HTTPS URL"
    )
    private String productThumbnail;

    // Quantité — entre 1 et 9999 comme Amazon
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 9999, message = "Quantity must not exceed 9999")
    private Integer quantity;

    // Prix unitaire — positif, max 10 millions
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.01", message = "Unit price must be greater than 0")
    @DecimalMax(value = "9999999.99", message = "Unit price must not exceed 9,999,999.99")
    @Digits(integer = 13, fraction = 2, message = "Unit price must have at most 13 integer digits and 2 decimal places")
    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    // Prix total — positif, cohérent avec quantity * unitPrice (vérifié en service)
    @NotNull(message = "Total price is required")
    @DecimalMin(value = "0.01", message = "Total price must be greater than 0")
    @DecimalMax(value = "99999999.99", message = "Total price must not exceed 99,999,999.99")
    @Digits(integer = 13, fraction = 2, message = "Total price must have at most 13 integer digits and 2 decimal places")
    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal totalPrice;

    // Variante libre — ex: "128GB / Noir" — pas de HTML
    @Size(max = 100, message = "Selected variant must not exceed 100 characters")
    @Pattern(
            regexp = "^[^<>{}]*$",
            message = "Selected variant must not contain HTML or special characters"
    )
    private String selectedVariant;

    // Couleur — lettres + espaces/tirets (ex: "Rouge vif", "Midnight Blue")
    @Size(max = 50, message = "Selected color must not exceed 50 characters")
    @Pattern(
            regexp = "^[\\p{L}0-9 '\\-#]*$",
            message = "Selected color contains invalid characters"
    )
    private String selectedColor;

    // Taille — alphanumérique (ex: "XL", "42", "10.5", "One Size")
    @Size(max = 20, message = "Selected size must not exceed 20 characters")
    @Pattern(
            regexp = "^[\\p{L}0-9 .\\-/]*$",
            message = "Selected size contains invalid characters"
    )
    private String selectedSize;

    // Vérification de cohérence quantity * unitPrice == totalPrice (à appeler en service)
    @Transient
    public boolean isTotalPriceConsistent() {
        if (quantity == null || unitPrice == null || totalPrice == null) return false;
        BigDecimal expected = unitPrice.multiply(BigDecimal.valueOf(quantity));
        return expected.compareTo(totalPrice) == 0;
    }
}