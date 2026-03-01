package tn.esprit.projetintegre.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetintegre.dto.request.CampHighlightRequest;
import tn.esprit.projetintegre.dto.response.CampHighlightResponse;
import tn.esprit.projetintegre.enums.HighlightCategory;
import tn.esprit.projetintegre.services.CampHighlightService;

import java.util.List;

@RestController
@RequestMapping("/api/camp-highlights")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CampHighlightController {

    private final CampHighlightService campHighlightService;

    @GetMapping("/site/{siteId}")
    public List<CampHighlightResponse> getHighlightsBySite(@PathVariable Long siteId) {
        return campHighlightService.getHighlightsBySite(siteId);
    }

    @GetMapping("/site/{siteId}/category/{category}")
    public List<CampHighlightResponse> getHighlightsBySiteAndCategory(@PathVariable Long siteId,
            @PathVariable HighlightCategory category) {
        return campHighlightService.getHighlightsBySiteAndCategory(siteId, category);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampHighlightResponse> getHighlightById(@PathVariable Long id) {
        return ResponseEntity.ok(campHighlightService.getHighlightById(id));
    }

    @PostMapping("/site/{siteId}")
    public ResponseEntity<CampHighlightResponse> createHighlight(@PathVariable Long siteId,
            @Valid @RequestBody CampHighlightRequest request) {
        return ResponseEntity.ok(campHighlightService.createHighlight(siteId, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CampHighlightResponse> updateHighlight(@PathVariable Long id,
            @Valid @RequestBody CampHighlightRequest request) {
        return ResponseEntity.ok(campHighlightService.updateHighlight(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHighlight(@PathVariable Long id) {
        campHighlightService.deleteHighlight(id);
        return ResponseEntity.noContent().build();
    }
}