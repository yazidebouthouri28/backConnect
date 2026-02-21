package com.camping.projet.dto.request;

import com.camping.projet.enums.ServiceType;
import com.camping.projet.enums.PricingUnit;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRequest {

    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal price;

    @NotNull
    private PricingUnit pricingUnit;

    @NotNull
    private ServiceType serviceType;

    @NotNull
    private Long campingId;
}
