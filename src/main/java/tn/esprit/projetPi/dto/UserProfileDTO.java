package tn.esprit.projetPi.dto;

import lombok.*;
import tn.esprit.projetPi.entities.Role;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileDTO {
    private String id;
    private String name;
    private String username;
    private String email;
    private String phone;
    private String address;
    private String country;
    private Long age;
    private Role role;
    
    // Profile fields
    private String avatar;
    private String bio;
    private String location;
    private String website;
    private List<String> interests;
    private Map<String, String> socialLinks;
    private Boolean emailVerified;
    private Boolean phoneVerified;
    private Boolean isActive;
    
    // Stats
    private Integer loyaltyPoints;
    private List<String> favoriteCampsites;
    private List<String> favoriteEvents;
    private Integer totalCampingTrips;
    private Integer totalEventsAttended;
    private Double averageRating;
    private Integer reviewCount;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}
