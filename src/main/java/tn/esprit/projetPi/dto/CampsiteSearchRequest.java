package tn.esprit.projetPi.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CampsiteSearchRequest {
    private String query;
    private String city;
    private String state;
    private String country;
    private Double minPrice;
    private Double maxPrice;
    private Integer guests;
    private List<String> amenities;
    private Double minRating;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Double latitude;
    private Double longitude;
    private Double radiusKm;
    private Boolean instantBooking;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}
