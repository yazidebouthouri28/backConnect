package tn.esprit.projetintegre.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import tn.esprit.projetintegre.enums.PromotionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionRequest {
    @NotBlank(message = "Promotion name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @NotNull(message = "Promotion type is required")
    private PromotionType type;

    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.01", message = "Discount value must be greater than 0")
    private BigDecimal discountValue;

    @DecimalMin(value = "0.0", message = "Min purchase amount must be positive or zero")
    private BigDecimal minPurchaseAmount;

    @DecimalMin(value = "0.0", message = "Max discount amount must be positive or zero")
    private BigDecimal maxDiscountAmount;

    @Min(value = 1, message = "Max usage must be at least 1")
    private Integer maxUsage;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    private LocalDateTime endDate;

    private Boolean isActive;
    private List<Long> applicableProductIds;
    private List<Long> applicableCategoryIds;
    private String targetAudience;
}
