package tn.esprit.projetintegre.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetintegre.entities.VirtualTour;
import tn.esprit.projetintegre.services.VirtualTourService;


import java.util.List;

@RestController
@RequestMapping("/api/virtual-tours")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VirtualTourController {

    private final VirtualTourService virtualTourService;

    @GetMapping("/site/{siteId}")
    public List<VirtualTour> getBySite(@PathVariable Long siteId) {
        return virtualTourService.getToursBySite(siteId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VirtualTour> getById(@PathVariable Long id) {
        return ResponseEntity.ok(virtualTourService.getTourById(id));
    }

    @PostMapping("/site/{siteId}")
    public ResponseEntity<VirtualTour> create(@PathVariable Long siteId,
                                              @RequestBody VirtualTour tour) {
        return ResponseEntity.ok(virtualTourService.createTour(siteId, tour));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VirtualTour> update(@PathVariable Long id,
                                              @RequestBody VirtualTour tour) {
        return ResponseEntity.ok(virtualTourService.updateTour(id, tour));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        virtualTourService.deleteTour(id);
        return ResponseEntity.noContent().build();
    }
}