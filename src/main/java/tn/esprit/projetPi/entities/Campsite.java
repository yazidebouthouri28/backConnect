package tn.esprit.projetPi.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "campsites")
public class Campsite {

    @Id
    String id;

    String name;
    String description;
    String shortDescription;
    
    // Location
    String address;
    String city;
    String state;
    String country;
    String zipCode;
    Double latitude;
    Double longitude;
    
    // Pricing
    Double pricePerNight;
    Double cleaningFee;
    Double serviceFee;
    String currency;
    
    // Amenities
    List<String> amenities;
    List<String> activities;
    List<String> rules;
    
    // Capacity
    Integer maxGuests;
    Integer tents;
    Integer cabins;
    Integer rvSpots;
    
    // Images
    List<String> images;
    String featuredImage;
    
    // Status
    Boolean isActive;
    Boolean isFeatured;
    Boolean instantBooking;
    
    // Ratings
    Double averageRating;
    Integer reviewCount;
    
    // Owner
    String ownerId;
    String ownerName;
    
    // Contact
    String phone;
    String email;
    String website;
    
    // Availability
    List<String> blockedDates;
    Integer minNights;
    Integer maxNights;
    
    // Timestamps
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
