package tn.esprit.projetintegre.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetintegre.dto.request.SiteRequest;
import tn.esprit.projetintegre.dto.response.SiteResponse;
import tn.esprit.projetintegre.services.SiteService;

import java.util.List;

@RestController
@RequestMapping("/api/sites")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SiteController {

    private final SiteService siteService;

    @GetMapping
    public List<SiteResponse> getAllSites() {
        return siteService.getAllSites();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SiteResponse> getSiteById(@PathVariable Long id) {
        return ResponseEntity.ok(siteService.getSiteById(id));
    }

    @GetMapping("/city/{city}")
    public List<SiteResponse> getSitesByCity(@PathVariable String city) {
        return siteService.getSitesByCity(city);
    }

    @GetMapping("/search")
    public List<SiteResponse> searchSites(@RequestParam String name) {
        return siteService.searchSitesByName(name);
    }

    @PostMapping
    public ResponseEntity<SiteResponse> createSite(@Valid @RequestBody SiteRequest request) {
        return ResponseEntity.ok(siteService.createSite(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SiteResponse> updateSite(@PathVariable Long id, @Valid @RequestBody SiteRequest request) {
        return ResponseEntity.ok(siteService.updateSite(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSite(@PathVariable Long id) {
        siteService.deleteSite(id);
        return ResponseEntity.noContent().build();
    }
}