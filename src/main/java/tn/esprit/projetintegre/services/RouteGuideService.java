package tn.esprit.projetintegre.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.projetintegre.entities.RouteGuide;
import tn.esprit.projetintegre.entities.Site;
import tn.esprit.projetintegre.repositories.RouteGuideRepository;
import tn.esprit.projetintegre.repositories.SiteRepository;


import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteGuideService {

    private final RouteGuideRepository routeGuideRepository;
    private final SiteRepository siteRepository;

    public List<RouteGuide> getRoutesBySite(Long siteId) {
        return routeGuideRepository.findBySite_SiteId(siteId);
    }

    public List<RouteGuide> getRoutesByOriginCity(String city) {
        return routeGuideRepository.findByOriginCityIgnoreCase(city);
    }

    public RouteGuide getRouteById(Long id) {
        return routeGuideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RouteGuide not found"));
    }

    public RouteGuide createRoute(Long siteId, RouteGuide route) {
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new RuntimeException("Site not found"));
        route.setSite(site);
        return routeGuideRepository.save(route);
    }

    public RouteGuide updateRoute(Long id, RouteGuide updated) {
        RouteGuide existing = getRouteById(id);
        existing.setOriginCity(updated.getOriginCity());
        existing.setDistanceKm(updated.getDistanceKm());
        existing.setDurationMin(updated.getDurationMin());
        existing.setInstructions(updated.getInstructions());
        existing.setMapUrl(updated.getMapUrl());
        return routeGuideRepository.save(existing);
    }

    public void deleteRoute(Long id) {
        routeGuideRepository.deleteById(id);
    }
}