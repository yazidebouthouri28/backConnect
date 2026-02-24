package tn.esprit.backconnect.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.persistence.*;

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
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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
    @Enumerated(EnumType.STRING)
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
    @ElementCollection
    List<String> interests;
    @ElementCollection
    @CollectionTable(name = "user_social_links")
    @MapKeyColumn(name = "platform")
    @Column(name = "url")
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
    @ElementCollection
    List<String> favoriteCampsites;
    @ElementCollection
    List<String> favoriteEvents;
    Integer totalCampingTrips;
    Integer totalEventsAttended;

    // Timestamps
    LocalDateTime createdAt;
    LocalDateTime lastLoginAt;
    LocalDateTime updatedAt;

    // Relations
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<ForumArticle> articles;

    @JsonIgnore
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    List<ChatMessage> messages;
}