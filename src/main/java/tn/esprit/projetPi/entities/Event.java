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
@Document(collection = "events")
public class Event {

    @Id
    String id;

    String title;
    String description;
    String shortDescription;
    EventType type;
    
    // Date and Time
    LocalDateTime startDate;
    LocalDateTime endDate;
    String timezone;
    
    // Location
    String venueName;
    String address;
    String city;
    String state;
    String country;
    Double latitude;
    Double longitude;
    Boolean isVirtual;
    String virtualLink;
    
    // Capacity and Registration
    Integer capacity;
    Integer registeredCount;
    Integer waitlistCount;
    Boolean registrationRequired;
    LocalDateTime registrationDeadline;
    
    // Pricing
    Double price;
    Boolean isFree;
    String currency;
    List<TicketTier> ticketTiers;
    
    // Images
    List<String> images;
    String featuredImage;
    
    // Organizer
    String organizerId;
    String organizerName;
    String organizerEmail;
    String organizerPhone;
    
    // Sponsors
    List<String> sponsorIds;
    
    // Status
    EventStatus status;
    Boolean isFeatured;
    Boolean isPublished;
    
    // Schedule/Agenda
    List<EventScheduleItem> schedule;
    
    // Ratings
    Double averageRating;
    Integer reviewCount;
    
    // Tags
    List<String> tags;
    List<String> categories;
    
    // Timestamps
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
