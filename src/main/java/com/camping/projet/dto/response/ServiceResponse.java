package com.camping.projet.dto.response;

import com.camping.projet.enums.ServiceType;
import com.camping.projet.enums.PricingUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private PricingUnit pricingUnit;
    private ServiceType serviceType;
    private boolean disponible;
    private Long campingId;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
}
