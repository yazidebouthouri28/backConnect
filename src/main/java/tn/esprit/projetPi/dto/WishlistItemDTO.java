package tn.esprit.projetPi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WishlistItemDTO {
    private String productId;
    private String productName;
    private String productImage;
    private BigDecimal priceWhenAdded;
    private BigDecimal currentPrice;
    private Boolean inStock;
    private LocalDateTime addedAt;
    private String notes;
}
