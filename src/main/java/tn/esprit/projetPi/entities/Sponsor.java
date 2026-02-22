package tn.esprit.projetPi.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "sponsors")
public class Sponsor {

    @Id
    String id;

    String name;
    String description;
    String logo;
    String website;
    String email;
    String phone;
    
    // Contact person
    String contactName;
    String contactEmail;
    String contactPhone;
    
    // Sponsorship details
    SponsorTier tier;
    Double sponsorshipAmount;
    String currency;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Boolean isActive;
    
    // Sponsored events
    List<String> sponsoredEventIds;
    
    // Benefits/Perks
    List<String> benefits;
    Map<String, String> socialLinks;
    
    // Assets
    List<String> banners;
    List<String> promotionalMaterials;
    
    // Timestamps
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
