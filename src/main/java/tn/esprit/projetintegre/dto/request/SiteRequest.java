package tn.esprit.projetintegre.dto.request;

import jakarta.validation.constraints.*;
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
    @NotBlank(message = "City is required")
    private String city;
    private String country;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private Double longitude;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal pricePerNight;
    private List<String> images;
    private List<String> amenities;
    private String contactPhone;
    private String contactEmail;
    private Boolean isActive;
    private Long ownerId;
}
