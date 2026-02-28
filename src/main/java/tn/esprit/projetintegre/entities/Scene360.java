package tn.esprit.projetintegre.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "scenes_360", indexes = {
        @Index(name = "idx_scene_tour", columnList = "virtual_tour_id"),
        @Index(name = "idx_scene_order", columnList = "orderIndex")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Scene360 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Column(length = 1000)
    private String description;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    private String thumbnailUrl;

    @Builder.Default
    @Min(value = 0, message = "Order index must be positive")
    private Integer orderIndex = 0;

    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private Double initialYaw;

    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private Double initialPitch;

    @Min(value = 10, message = "FOV must be at least 10")
    @Max(value = 120, message = "FOV cannot exceed 120")
    @Builder.Default
    private Integer initialFov = 90;

    @ElementCollection
    @CollectionTable(name = "scene_hotspots", joinColumns = @JoinColumn(name = "scene_id"))
    @Column(name = "hotspot_data", length = 1000)
    @Builder.Default
    private List<String> hotspots = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "virtual_tour_id", nullable = false)
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