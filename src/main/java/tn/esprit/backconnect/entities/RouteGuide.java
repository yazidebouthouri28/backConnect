package tn.esprit.backconnect.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "route_guides")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteGuide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routeGuideId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "siteId", nullable = false)
    private Site site;

    @Column(nullable = false)
    private String originCity;

    @Column(nullable = false)
    private Double distanceKm;

    @Column(nullable = false)
    private Integer durationMin;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String instructions; // JSON format for steps

    private String mapUrl;
}