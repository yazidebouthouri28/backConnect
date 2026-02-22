package tn.esprit.projetPi.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.projetPi.dto.*;
import tn.esprit.projetPi.entities.*;
import tn.esprit.projetPi.repositories.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;
    private final ProductReviewRepository reviewRepository;
    private final CouponRepository couponRepository;
    private final RefundRequestRepository refundRepository;
    private final WalletRepository walletRepository;
    private final CategoryRepository categoryRepository;

    public AdminDashboardDTO getDashboard() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfToday = now.with(LocalTime.MIN);
        LocalDateTime startOfWeek = now.minusDays(7);
        LocalDateTime startOfMonth = now.minusDays(30);

        AdminDashboardDTO dashboard = new AdminDashboardDTO();

        // User stats
        dashboard.setTotalUsers(userRepository.count());
        dashboard.setTotalSellers(userRepository.countByIsSeller(true));
        dashboard.setTotalBuyers(userRepository.countByRole(Role.BUYER));
        dashboard.setNewUsersToday(userRepository.countNewUsersAfter(startOfToday));
        dashboard.setNewUsersThisWeek(userRepository.countNewUsersAfter(startOfWeek));
        dashboard.setNewUsersThisMonth(userRepository.countNewUsersAfter(startOfMonth));
        dashboard.setActiveUsers(userRepository.countByIsActive(true));
        dashboard.setSuspendedUsers(userRepository.countByIsSuspended(true));

        // Order stats
        dashboard.setTotalOrders(orderRepository.count());
        dashboard.setPendingOrders(orderRepository.countByStatus(OrderStatus.PENDING));
        dashboard.setProcessingOrders(orderRepository.countByStatus(OrderStatus.PROCESSING));
        dashboard.setShippedOrders(orderRepository.countByStatus(OrderStatus.SHIPPED));
        dashboard.setDeliveredOrders(orderRepository.countByStatus(OrderStatus.DELIVERED));
        dashboard.setCancelledOrders(orderRepository.countByStatus(OrderStatus.CANCELLED));
        dashboard.setOrdersToday(orderRepository.countOrdersAfter(startOfToday));
        dashboard.setOrdersThisWeek(orderRepository.countOrdersAfter(startOfWeek));
        dashboard.setOrdersThisMonth(orderRepository.countOrdersAfter(startOfMonth));

        // Revenue stats
        List<Order> allOrders = orderRepository.findAll();
        BigDecimal totalRevenue = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dashboard.setTotalRevenue(totalRevenue);

        BigDecimal revenueToday = orderRepository.findByOrderDateBetween(startOfToday, now).stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dashboard.setRevenueToday(revenueToday);

        BigDecimal revenueThisWeek = orderRepository.findByOrderDateBetween(startOfWeek, now).stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dashboard.setRevenueThisWeek(revenueThisWeek);

        BigDecimal revenueThisMonth = orderRepository.findByOrderDateBetween(startOfMonth, now).stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dashboard.setRevenueThisMonth(revenueThisMonth);

        long deliveredCount = dashboard.getDeliveredOrders();
        if (deliveredCount > 0) {
            dashboard.setAverageOrderValue(totalRevenue.divide(BigDecimal.valueOf(deliveredCount), 2, java.math.RoundingMode.HALF_UP));
        } else {
            dashboard.setAverageOrderValue(BigDecimal.ZERO);
        }

        // Product stats
        dashboard.setTotalProducts(productRepository.count());
        dashboard.setActiveProducts(productRepository.countByIsActive(true));
        dashboard.setPendingApprovalProducts(productRepository.countByIsApproved(false));
        dashboard.setOutOfStockProducts((long) productRepository.findOutOfStockProducts().size());
        dashboard.setLowStockProducts((long) productRepository.findLowStockProducts(10).size());

        // Review stats
        dashboard.setTotalReviews(reviewRepository.count());
        dashboard.setPendingReviews((long) reviewRepository.findByApproved(false).size());
        double avgRating = reviewRepository.findAll().stream()
                .filter(r -> Boolean.TRUE.equals(r.getApproved()))
                .mapToInt(ProductReview::getRating)
                .average()
                .orElse(0.0);
        dashboard.setAverageRating(Math.round(avgRating * 10.0) / 10.0);

        // Refund stats
        dashboard.setTotalRefundRequests(refundRepository.count());
        dashboard.setPendingRefundRequests(refundRepository.countByStatus(RefundStatus.PENDING));
        BigDecimal totalRefunded = refundRepository.findByStatus(RefundStatus.COMPLETED).stream()
                .map(RefundRequest::getApprovedAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dashboard.setTotalRefundAmount(totalRefunded);

        // Transaction stats
        dashboard.setTotalTransactions(transactionRepository.count());
        BigDecimal totalWalletBalance = walletRepository.findAll().stream()
                .map(Wallet::getBalance)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dashboard.setTotalWalletBalance(totalWalletBalance);

        // Top selling products
        List<Product> topProducts = productRepository.findTop10ByOrderByPurchaseCountDesc();
        dashboard.setTopSellingProducts(topProducts.stream()
                .map(this::toProductDTO)
                .collect(Collectors.toList()));

        // Top categories
        List<Category> topCategories = categoryRepository.findByIsActiveOrderBySortOrderAsc(true);
        dashboard.setTopCategories(topCategories.stream()
                .limit(10)
                .map(this::toCategoryDTO)
                .collect(Collectors.toList()));

        return dashboard;
    }

    public Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalUsers", userRepository.count());
        stats.put("totalProducts", productRepository.count());
        stats.put("totalOrders", orderRepository.count());
        stats.put("totalTransactions", transactionRepository.count());
        stats.put("totalReviews", reviewRepository.count());
        stats.put("totalCoupons", couponRepository.count());
        stats.put("activeCoupons", couponRepository.findByIsActive(true).size());
        stats.put("totalCategories", categoryRepository.count());
        
        // Order breakdown by status
        Map<String, Long> ordersByStatus = new HashMap<>();
        for (OrderStatus status : OrderStatus.values()) {
            ordersByStatus.put(status.name(), orderRepository.countByStatus(status));
        }
        stats.put("ordersByStatus", ordersByStatus);
        
        // User breakdown by role
        Map<String, Long> usersByRole = new HashMap<>();
        for (Role role : Role.values()) {
            usersByRole.put(role.name(), userRepository.countByRole(role));
        }
        stats.put("usersByRole", usersByRole);
        
        return stats;
    }

    public Map<String, BigDecimal> getRevenueAnalytics(int days) {
        Map<String, BigDecimal> revenueByDay = new LinkedHashMap<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = days - 1; i >= 0; i--) {
            LocalDateTime dayStart = now.minusDays(i).with(LocalTime.MIN);
            LocalDateTime dayEnd = now.minusDays(i).with(LocalTime.MAX);
            
            BigDecimal dayRevenue = orderRepository.findByOrderDateBetween(dayStart, dayEnd).stream()
                    .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                    .map(Order::getTotalAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            String dateKey = dayStart.toLocalDate().toString();
            revenueByDay.put(dateKey, dayRevenue);
        }

        return revenueByDay;
    }

    public Map<String, Long> getOrderAnalytics(int days) {
        Map<String, Long> ordersByDay = new LinkedHashMap<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = days - 1; i >= 0; i--) {
            LocalDateTime dayStart = now.minusDays(i).with(LocalTime.MIN);
            LocalDateTime dayEnd = now.minusDays(i).with(LocalTime.MAX);
            
            long dayOrders = orderRepository.findByOrderDateBetween(dayStart, dayEnd).size();
            
            String dateKey = dayStart.toLocalDate().toString();
            ordersByDay.put(dateKey, dayOrders);
        }

        return ordersByDay;
    }

    public List<SellerStatsDTO> getTopSellers(int limit) {
        List<User> sellers = userRepository.findByIsSeller(true).stream()
                .sorted((a, b) -> {
                    int salesA = a.getTotalSales() != null ? a.getTotalSales() : 0;
                    int salesB = b.getTotalSales() != null ? b.getTotalSales() : 0;
                    return Integer.compare(salesB, salesA);
                })
                .limit(limit)
                .collect(Collectors.toList());

        return sellers.stream().map(this::toSellerStatsDTO).collect(Collectors.toList());
    }

    private SellerStatsDTO toSellerStatsDTO(User seller) {
        long totalProducts = productRepository.countBySellerId(seller.getId());
        long activeProducts = productRepository.countActiveProductsBySeller(seller.getId());
        long totalOrders = orderRepository.countBySellerId(seller.getId());
        long completedOrders = orderRepository.countBySellerIdAndStatus(seller.getId(), OrderStatus.DELIVERED);

        BigDecimal totalRevenue = orderRepository.findBySellerId(seller.getId()).stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return SellerStatsDTO.builder()
                .sellerId(seller.getId())
                .sellerName(seller.getName())
                .storeName(seller.getStoreName())
                .storeDescription(seller.getStoreDescription())
                .storeLogo(seller.getStoreLogo())
                .totalProducts(totalProducts)
                .activeProducts(activeProducts)
                .outOfStockProducts(totalProducts - activeProducts)
                .totalOrders(totalOrders)
                .completedOrders(completedOrders)
                .totalRevenue(totalRevenue)
                .sellerRating(seller.getSellerRating() != null ? seller.getSellerRating().doubleValue() : 0.0)
                .reviewCount(seller.getSellerReviewCount())
                .sellerSince(seller.getSellerSince())
                .verified(seller.getSellerVerified())
                .build();
    }

    private ProductDTO toProductDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .mainImage(product.getMainImage())
                .purchaseCount(product.getPurchaseCount())
                .averageRating(product.getAverageRating())
                .reviewCount(product.getReviewCount())
                .sellerId(product.getSellerId())
                .sellerName(product.getSellerName())
                .build();
    }

    private CategoryDTO toCategoryDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .image(category.getImage())
                .productCount(category.getProductCount())
                .build();
    }
}
