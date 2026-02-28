package tn.esprit.projetintegre.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetintegre.entities.Scene360;
import tn.esprit.projetintegre.services.Scene360Service;


import java.util.List;

@RestController
@RequestMapping("/api/scenes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class Scene360Controller {

    private final Scene360Service scene360Service;

    @GetMapping("/tour/{tourId}")
    public List<Scene360> getByTour(@PathVariable Long tourId) {
        return scene360Service.getScenesByTour(tourId);
    }

    @PostMapping("/tour/{tourId}")
    public ResponseEntity<Scene360> add(@PathVariable Long tourId,
                                        @RequestBody Scene360 scene) {
        return ResponseEntity.ok(scene360Service.addScene(tourId, scene));
    }

    @PutMapping("/{sceneId}")
    public ResponseEntity<Scene360> update(@PathVariable Long sceneId,
                                           @RequestBody Scene360 scene) {
        return ResponseEntity.ok(scene360Service.updateScene(sceneId, scene));
    }

    @DeleteMapping("/{sceneId}")
    public ResponseEntity<Void> delete(@PathVariable Long sceneId) {
        scene360Service.deleteScene(sceneId);
        return ResponseEntity.noContent().build();
    }
}