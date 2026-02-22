package tn.esprit.projetPi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.projetPi.entities.Role;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String id;
    private String name;
    private String username;
    private String email;
    private String phone;
    private String address;
    private String country;
    private Long age;

    private Role role;
    private Boolean isSeller;
    private Boolean isBuyer;
    private Boolean isAdmin;
    
    // Seller specific
    private String storeName;
    private String storeDescription;
    private String storeLogo;
    private BigDecimal sellerRating;
    private Integer sellerReviewCount;
    private Integer totalSales;
    private Boolean sellerVerified;
    private LocalDateTime sellerSince;
    
    // Account status
    private Boolean isActive;
    private Boolean isSuspended;
    private String suspensionReason;
    
    // Profile
    private String avatar;
    private String bio;
    private String location;
    private String website;
    private List<String> interests;
    private Map<String, String> socialLinks;
    
    // Verification
    private Boolean emailVerified;
    private Boolean phoneVerified;
    
    // Loyalty
    private Integer loyaltyPoints;
    private String loyaltyTier;
    
    // Shipping
    private List<ShippingAddressDTO> shippingAddresses;
    private String defaultShippingAddressId;
    
    // Stats
    private Integer totalOrders;
    private BigDecimal totalSpent;
    private Double averageRating;
    private Integer reviewCount;
    
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}
