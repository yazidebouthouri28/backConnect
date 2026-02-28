package tn.esprit.projetintegre.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "route_guides", indexes = {
        @Index(name = "idx_route_site", columnList = "site_id"),
        @Index(name = "idx_route_tour", columnList = "virtual_tour_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RouteGuide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 200, message = "Name cannot exceed 200 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Column(length = 1000)
    private String description;

    @NotBlank(message = "Origin city is required")
    private String originCity;

    @Min(value = 0, message = "Distance must be positive")
    private Double distanceKm;

    @Min(value = 0, message = "Duration must be positive")
    private Integer estimatedDurationMinutes;

    @Size(max = 50)
    private String difficulty; // EASY, MEDIUM, HARD

    @Column(columnDefinition = "TEXT")
    private String instructions; // JSON steps

    private String mapUrl;

    @ElementCollection
    @CollectionTable(name = "route_waypoints", joinColumns = @JoinColumn(name = "route_id"))
    @Column(name = "waypoint_data", length = 500)
    @Builder.Default
    private List<String> waypoints = new ArrayList<>();

    @Builder.Default
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "virtual_tour_id")
    private VirtualTour virtualTour;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}