package tn.esprit.projetintegre.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteRequest {
    @NotBlank(message = "Site name is required")
    private String name;
    
    private String description;
    private String type;
    private String address;
    private String city;
    private String country;
    private Double latitude;
    private Double longitude;
    private Integer capacity;
    private BigDecimal pricePerNight;
    private List<String> images;
    private List<String> amenities;
    private String contactPhone;
    private String contactEmail;
    private Boolean isActive;
    private Long ownerId;
}
