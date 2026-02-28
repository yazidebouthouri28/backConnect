package tn.esprit.projetintegre.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetintegre.entities.CampHighlight;
import tn.esprit.projetintegre.enums.HighlightCategory;
import tn.esprit.projetintegre.services.CampHighlightService;


import java.util.List;

@RestController
@RequestMapping("/api/highlights")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CampHighlightController {

    private final CampHighlightService campHighlightService;

    @GetMapping("/site/{siteId}")
    public List<CampHighlight> getBySite(@PathVariable Long siteId) {
        return campHighlightService.getHighlightsBySite(siteId);
    }

    @GetMapping("/site/{siteId}/category/{category}")
    public List<CampHighlight> getBySiteAndCategory(@PathVariable Long siteId,
                                                    @PathVariable HighlightCategory category) {
        return campHighlightService.getHighlightsBySiteAndCategory(siteId, category);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampHighlight> getById(@PathVariable Long id) {
        return ResponseEntity.ok(campHighlightService.getHighlightById(id));
    }

    @PostMapping("/site/{siteId}")
    public ResponseEntity<CampHighlight> create(@PathVariable Long siteId,
                                                @RequestBody CampHighlight highlight) {
        return ResponseEntity.ok(campHighlightService.createHighlight(siteId, highlight));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CampHighlight> update(@PathVariable Long id,
                                                @RequestBody CampHighlight highlight) {
        return ResponseEntity.ok(campHighlightService.updateHighlight(id, highlight));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        campHighlightService.deleteHighlight(id);
        return ResponseEntity.noContent().build();
    }
}