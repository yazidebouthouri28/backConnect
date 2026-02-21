package com.camping.projet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal packPrice;
    private BigDecimal normalPrice;
    private BigDecimal savingsPercentage;
    private Set<ServiceResponse> services;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;
}
