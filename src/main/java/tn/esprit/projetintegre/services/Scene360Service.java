package tn.esprit.projetintegre.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.projetintegre.entities.Scene360;
import tn.esprit.projetintegre.entities.VirtualTour;
import tn.esprit.projetintegre.repositories.Scene360Repository;
import tn.esprit.projetintegre.repositories.VirtualTourRepository;


import java.util.List;

@Service
@RequiredArgsConstructor
public class Scene360Service  {

    private final Scene360Repository scene360Repository;
    private final VirtualTourRepository virtualTourRepository;

    public List<Scene360> getScenesByTour(Long tourId) {
        return scene360Repository.findByTour_VirtualTourIdOrderBySceneOrderAsc(tourId);
    }

    public Scene360 addScene(Long tourId, Scene360 scene) {
        VirtualTour tour = virtualTourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("VirtualTour not found"));
        scene.setTour(tour);
        return scene360Repository.save(scene);
    }

    public Scene360 updateScene(Long sceneId, Scene360 updated) {
        Scene360 existing = scene360Repository.findById(sceneId)
                .orElseThrow(() -> new RuntimeException("Scene not found"));
        existing.setName(updated.getName());
        existing.setPanoramaUrl(updated.getPanoramaUrl());
        existing.setSceneOrder(updated.getSceneOrder());
        return scene360Repository.save(existing);
    }

    public void deleteScene(Long sceneId) {
        scene360Repository.deleteById(sceneId);
    }
}