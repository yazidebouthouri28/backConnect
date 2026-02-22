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
public class SellerStatsDTO {
    private String sellerId;
    private String sellerName;
    private String storeName;
    private String storeDescription;
    private String storeLogo;
    
    private Long totalProducts;
    private Long activeProducts;
    private Long outOfStockProducts;
    
    private Long totalOrders;
    private Long pendingOrders;
    private Long completedOrders;
    private Long cancelledOrders;
    
    private BigDecimal totalRevenue;
    private BigDecimal revenueThisMonth;
    private BigDecimal averageOrderValue;
    
    private Double sellerRating;
    private Integer reviewCount;
    
    private Long totalCustomers;
    private Long returningCustomers;
    
    private LocalDateTime sellerSince;
    private Boolean verified;
}
