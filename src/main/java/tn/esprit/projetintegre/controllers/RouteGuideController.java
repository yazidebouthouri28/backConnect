package tn.esprit.projetintegre.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetintegre.entities.RouteGuide;
import tn.esprit.projetintegre.services.RouteGuideService;


import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RouteGuideController {

    private final RouteGuideService routeGuideService;

    @GetMapping("/site/{siteId}")
    public List<RouteGuide> getBySite(@PathVariable Long siteId) {
        return routeGuideService.getRoutesBySite(siteId);
    }

    @GetMapping("/origin/{city}")
    public List<RouteGuide> getByOriginCity(@PathVariable String city) {
        return routeGuideService.getRoutesByOriginCity(city);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RouteGuide> getById(@PathVariable Long id) {
        return ResponseEntity.ok(routeGuideService.getRouteById(id));
    }

    @PostMapping("/site/{siteId}")
    public ResponseEntity<RouteGuide> create(@PathVariable Long siteId,
                                             @RequestBody RouteGuide route) {
        return ResponseEntity.ok(routeGuideService.createRoute(siteId, route));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RouteGuide> update(@PathVariable Long id,
                                             @RequestBody RouteGuide route) {
        return ResponseEntity.ok(routeGuideService.updateRoute(id, route));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        routeGuideService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }
}