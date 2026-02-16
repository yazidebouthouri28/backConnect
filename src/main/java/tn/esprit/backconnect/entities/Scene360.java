package tn.esprit.backconnect.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "scenes_360")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Scene360 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sceneId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tourId", nullable = false)
    private VirtualTour tour;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String panoramaUrl;

    @Column(nullable = false)
    private Integer sceneOrder;
}
