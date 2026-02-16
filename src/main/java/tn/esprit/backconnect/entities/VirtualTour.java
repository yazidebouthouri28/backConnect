package tn.esprit.backconnect.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "virtual_tours")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VirtualTour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long virtualTourId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "siteId", nullable = false)
    private Site site;

    @Column(nullable = false)
    private String title;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Scene360> scenes = new ArrayList<>();
}