package tn.esprit.projetPi.dto;

import lombok.*;
import tn.esprit.projetPi.entities.SponsorTier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SponsorDTO {
    private String id;
    private String name;
    private String description;
    private String logo;
    private String website;
    private String email;
    private String phone;
    
    // Contact person
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    
    // Sponsorship details
    private SponsorTier tier;
    private Double sponsorshipAmount;
    private String currency;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;
    
    // Sponsored events
    private List<String> sponsoredEventIds;
    private Integer sponsoredEventsCount;
    
    // Benefits/Perks
    private List<String> benefits;
    private Map<String, String> socialLinks;
    
    // Assets
    private List<String> banners;
    private List<String> promotionalMaterials;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
