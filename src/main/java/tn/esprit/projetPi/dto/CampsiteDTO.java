package tn.esprit.projetPi.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CampsiteDTO {
    private String id;
    private String name;
    private String description;
    private String shortDescription;
    
    // Location
    private String address;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private Double latitude;
    private Double longitude;
    
    // Pricing
    private Double pricePerNight;
    private Double cleaningFee;
    private Double serviceFee;
    private String currency;
    
    // Amenities
    private List<String> amenities;
    private List<String> activities;
    private List<String> rules;
    
    // Capacity
    private Integer maxGuests;
    private Integer tents;
    private Integer cabins;
    private Integer rvSpots;
    
    // Images
    private List<String> images;
    private String featuredImage;
    
    // Status
    private Boolean isActive;
    private Boolean isFeatured;
    private Boolean instantBooking;
    
    // Ratings
    private Double averageRating;
    private Integer reviewCount;
    
    // Owner
    private String ownerId;
    private String ownerName;
    
    // Contact
    private String phone;
    private String email;
    private String website;
    
    // Availability
    private List<String> blockedDates;
    private Integer minNights;
    private Integer maxNights;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
