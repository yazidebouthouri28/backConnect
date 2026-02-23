package tn.esprit.projetPi.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "users")
public class User {

    @Id
    String id;

    // Basic info
    String name;
    String username;
    String email;
    String password;
    String phone;
    String address;
    String country;
    Long age;

    // Role management
    Role role;
    Boolean isSeller;
    Boolean isBuyer;
    Boolean isAdmin;
    
    // Seller specific fields
    String storeName;
    String storeDescription;
    String storeLogo;
    String storeBanner;
    BigDecimal sellerRating;
    Integer sellerReviewCount;
    Integer totalSales;
    BigDecimal totalRevenue;
    Boolean sellerVerified;
    LocalDateTime sellerSince;
    
    // Account status
    Boolean isActive;
    Boolean isSuspended;
    String suspensionReason;
    LocalDateTime suspendedAt;
    LocalDateTime suspendedUntil;

    // Profile fields
    String avatar;
    String bio;
    String location;
    String website;
    List<String> interests;
    Map<String, String> socialLinks;
    
    // Verification
    Boolean emailVerified;
    Boolean phoneVerified;
    String emailVerificationToken;
    String passwordResetToken;
    LocalDateTime passwordResetExpires;
    
    // Loyalty
    Integer loyaltyPoints;
    String loyaltyTier; // BRONZE, SILVER, GOLD, PLATINUM
    
    // Shipping addresses
    List<ShippingAddress> shippingAddresses = new ArrayList<>();
    String defaultShippingAddressId;
    String defaultBillingAddressId;
    
    // Statistics
    Integer totalOrders;
    BigDecimal totalSpent;
    Integer reviewsGiven;
    Integer reviewsReceived;
    Double averageRating;
    Integer reviewCount;
    
    // Camping preferences (kept for compatibility)
    List<String> favoriteCampsites;
    List<String> favoriteEvents;
    Integer totalCampingTrips;
    Integer totalEventsAttended;

    // Timestamps
    LocalDateTime createdAt;
    LocalDateTime lastLoginAt;
    LocalDateTime updatedAt;

    // Relations
    @DBRef
    List<ForumArticle> articles;

    @DBRef
    List<ChatMessage> messages;
}
