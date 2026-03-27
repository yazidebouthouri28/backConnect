package tn.esprit.projetintegre.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tn.esprit.projetintegre.enums.Role;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 150)
    @Column(unique = true)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6)
    private String password;

    private String phone;
    
    @Column(length = 500)
    private String address;
    
    private String country;
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    @Builder.Default
    private Boolean isSeller = false;
    @Builder.Default
    private Boolean isBuyer = true;
    @Builder.Default
    private Boolean isAdmin = false;

    // Seller specific fields
    private String storeName;
    
    @Column(length = 1000)
    private String storeDescription;
    
    private String storeLogo;
    private String storeBanner;
    
    @Column(precision = 3, scale = 2)
    private BigDecimal sellerRating;
    
    @Builder.Default
    private Integer sellerReviewCount = 0;
    @Builder.Default
    private Integer totalSales = 0;
    
    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalRevenue = BigDecimal.ZERO;
    
    @Builder.Default
    private Boolean sellerVerified = false;
    private LocalDateTime sellerSince;

    // Account status
    @Builder.Default
    private Boolean isActive = true;
    @Builder.Default
    private Boolean isSuspended = false;
    private String suspensionReason;
    private LocalDateTime suspendedAt;
    private LocalDateTime suspendedUntil;

    // Profile fields
    private String avatar;
    
    @Column(length = 500)
    private String bio;
    
    private String location;
    private String website;

    // Verification
    @Builder.Default
    private Boolean emailVerified = false;
    @Builder.Default
    private Boolean phoneVerified = false;
    private String emailVerificationToken;
    private String passwordResetToken;
    private LocalDateTime passwordResetExpires;

    // Loyalty
    @Builder.Default
    private Integer loyaltyPoints = 0;
    @Builder.Default
    private String loyaltyTier = "BRONZE";

    // Gamification
    @Builder.Default
    private Integer experiencePoints = 0;
    @Builder.Default
    private Integer level = 1;
    @Builder.Default
    private Integer totalMissionsCompleted = 0;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime lastLogin;

    // Relations
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Wallet wallet;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Cart cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ProductReview> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Subscription> subscriptions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Transaction> transactions = new ArrayList<>();
}
