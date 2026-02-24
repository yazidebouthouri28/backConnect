package tn.esprit.backconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardDTO {
    // User stats
    private Long totalUsers;
    private Long totalSellers;
    private Long totalBuyers;
    private Long newUsersToday;
    private Long newUsersThisWeek;
    private Long newUsersThisMonth;
    private Long activeUsers;
    private Long suspendedUsers;
    
    // Order stats
    private Long totalOrders;
    private Long pendingOrders;
    private Long processingOrders;
    private Long shippedOrders;
    private Long deliveredOrders;
    private Long cancelledOrders;
    private Long ordersToday;
    private Long ordersThisWeek;
    private Long ordersThisMonth;
    
    // Revenue stats
    private BigDecimal totalRevenue;
    private BigDecimal revenueToday;
    private BigDecimal revenueThisWeek;
    private BigDecimal revenueThisMonth;
    private BigDecimal averageOrderValue;
    
    // Product stats
    private Long totalProducts;
    private Long activeProducts;
    private Long pendingApprovalProducts;
    private Long outOfStockProducts;
    private Long lowStockProducts;
    
    // Review stats
    private Long totalReviews;
    private Long pendingReviews;
    private Double averageRating;
    
    // Refund stats
    private Long totalRefundRequests;
    private Long pendingRefundRequests;
    private BigDecimal totalRefundAmount;
    
    // Transaction stats
    private Long totalTransactions;
    private BigDecimal totalWalletBalance;

    // Charts data
    private Map<String, BigDecimal> revenueByDay;
    private Map<String, Long> ordersByDay;
    private Map<String, Long> userRegistrationsByDay;
}
