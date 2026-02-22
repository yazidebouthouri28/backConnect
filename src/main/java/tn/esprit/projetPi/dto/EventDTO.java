package tn.esprit.projetPi.dto;

import lombok.*;
import tn.esprit.projetPi.entities.EventScheduleItem;
import tn.esprit.projetPi.entities.EventStatus;
import tn.esprit.projetPi.entities.EventType;
import tn.esprit.projetPi.entities.TicketTier;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDTO {
    private String id;
    private String title;
    private String description;
    private String shortDescription;
    private EventType type;
    
    // Date and Time
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String timezone;
    
    // Location
    private String venueName;
    private String address;
    private String city;
    private String state;
    private String country;
    private Double latitude;
    private Double longitude;
    private Boolean isVirtual;
    private String virtualLink;
    
    // Capacity and Registration
    private Integer capacity;
    private Integer registeredCount;
    private Integer waitlistCount;
    private Boolean registrationRequired;
    private LocalDateTime registrationDeadline;
    private Integer availableSpots;
    
    // Pricing
    private Double price;
    private Boolean isFree;
    private String currency;
    private List<TicketTier> ticketTiers;
    
    // Images
    private List<String> images;
    private String featuredImage;
    
    // Organizer
    private String organizerId;
    private String organizerName;
    private String organizerEmail;
    private String organizerPhone;
    
    // Sponsors
    private List<String> sponsorIds;
    private List<SponsorDTO> sponsors;
    
    // Status
    private EventStatus status;
    private Boolean isFeatured;
    private Boolean isPublished;
    
    // Schedule/Agenda
    private List<EventScheduleItem> schedule;
    
    // Ratings
    private Double averageRating;
    private Integer reviewCount;
    
    // Tags
    private List<String> tags;
    private List<String> categories;
    
    // User-specific
    private Boolean isRegistered;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
