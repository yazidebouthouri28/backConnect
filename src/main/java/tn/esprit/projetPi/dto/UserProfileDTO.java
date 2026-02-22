package tn.esprit.projetPi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.projetPi.entities.Role;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    private String avatar;
    private String bio;
    private String location;
    private String website;
    private List<String> interests;
    private Map<String, String> socialLinks;
    private Boolean emailVerified;
    private Boolean phoneVerified;
    private Boolean isActive;
    private Integer loyaltyPoints;
    private List<String> favoriteCampsites;
    private List<String> favoriteEvents;
    private Integer totalCampingTrips;
    private Integer totalEventsAttended;
    private Double averageRating;
    private Integer reviewCount;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}
