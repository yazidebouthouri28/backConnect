package tn.esprit.orderservice.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "shipping_addresses", indexes = {
        @Index(name = "idx_shipaddr_user", columnList = "user_id"),
        @Index(name = "idx_shipaddr_default", columnList = "is_default")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    // Ex: "Maison", "Bureau", "Chez maman"
    @Size(max = 50, message = "Label must not exceed 50 characters")
    @Pattern(
            regexp = "^[\\p{L}0-9 _\\-]*$",
            message = "Label contains invalid characters"
    )
    private String label;

    // Prénom + Nom complet — lettres, espaces, tirets, apostrophes
    @NotBlank(message = "Recipient name is required")
    @Size(min = 2, max = 100, message = "Recipient name must be between 2 and 100 characters")
    @Pattern(
            regexp = "^[\\p{L} '\\-]+$",
            message = "Recipient name must contain letters only (spaces, hyphens and apostrophes allowed)"
    )
    private String recipientName;

    // Format international : +216 XX XXX XXX ou local
    @Pattern(
            regexp = "^(\\+?[0-9]{1,3}[\\s\\-]?)?[0-9]{6,14}$",
            message = "Phone number is invalid. Example: +216 98 123 456"
    )
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phone;

    // Numéro + rue — doit commencer par un chiffre ou une lettre
    @NotBlank(message = "Address line 1 is required")
    @Size(min = 5, max = 200, message = "Address line 1 must be between 5 and 200 characters")
    @Pattern(
            regexp = "^[\\p{L}0-9][\\p{L}0-9 ,.'\\-#/]*$",
            message = "Address line 1 contains invalid characters"
    )
    private String addressLine1;

    // Optionnel : appartement, étage, bâtiment
    @Size(max = 200, message = "Address line 2 must not exceed 200 characters")
    @Pattern(
            regexp = "^[\\p{L}0-9 ,.'\\-#/]*$",
            message = "Address line 2 contains invalid characters"
    )
    private String addressLine2;

    // Ville — lettres uniquement
    @NotBlank(message = "City is required")
    @Size(min = 2, max = 100, message = "City must be between 2 and 100 characters")
    @Pattern(
            regexp = "^[\\p{L} '\\-]+$",
            message = "City must contain letters only"
    )
    private String city;

    // Gouvernorat / État — optionnel
    @Size(max = 100, message = "State must not exceed 100 characters")
    @Pattern(
            regexp = "^[\\p{L} '\\-]*$",
            message = "State must contain letters only"
    )
    private String state;

    // Code postal tunisien : 4 chiffres / international : 3–10 alphanum
    @NotBlank(message = "Postal code is required")
    @Size(min = 3, max = 20, message = "Postal code must be between 3 and 20 characters")
    @Pattern(
            regexp = "^[A-Z0-9]{3,10}([ \\-][A-Z0-9]{3,7})?$",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Postal code is invalid. Example: 1002 or SW1A 1AA"
    )
    private String postalCode;

    // Pays — lettres uniquement, pas de chiffres
    @NotBlank(message = "Country is required")
    @Size(min = 2, max = 100, message = "Country must be between 2 and 100 characters")
    @Pattern(
            regexp = "^[\\p{L} '\\-]+$",
            message = "Country must contain letters only"
    )
    private String country;

    // Instructions libres — texte brut, pas de HTML/scripts
    @Size(max = 500, message = "Delivery instructions must not exceed 500 characters")
    @Pattern(
            regexp = "^[^<>{}]*$",
            message = "Delivery instructions must not contain HTML or special characters like < > { }"
    )
    @Column(length = 500)
    private String deliveryInstructions;

    @Column(name = "is_default")
    @Builder.Default
    private Boolean isDefault = false;

    @Builder.Default
    private Boolean isActive = true;

    @NotNull(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(updatable = false)
    private LocalDateTime createdAt;

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

    // Nettoyage automatique avant persistance
    private void normalizeFields() {
        if (recipientName != null) recipientName = recipientName.trim();
        if (addressLine1 != null)  addressLine1  = addressLine1.trim();
        if (addressLine2 != null)  addressLine2  = addressLine2.trim();
        if (city != null)          city          = city.trim();
        if (state != null)         state         = state.trim();
        if (postalCode != null)    postalCode    = postalCode.trim().toUpperCase();
        if (country != null)       country       = country.trim();
        if (phone != null)         phone         = phone.trim().replaceAll("\\s+", " ");
        if (label != null)         label         = label.trim();
    }

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(addressLine1);
        if (addressLine2 != null && !addressLine2.isBlank()) sb.append(", ").append(addressLine2);
        sb.append(", ").append(city);
        if (state != null && !state.isBlank()) sb.append(", ").append(state);
        sb.append(" ").append(postalCode);
        sb.append(", ").append(country);
        return sb.toString();
    }
}