package tn.esprit.projetintegre.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.projetintegre.entities.Site;
import tn.esprit.projetintegre.entities.VirtualTour;
import tn.esprit.projetintegre.repositories.SiteRepository;
import tn.esprit.projetintegre.repositories.VirtualTourRepository;


import java.util.List;

@Service
@RequiredArgsConstructor
public class VirtualTourService  {

    private final VirtualTourRepository virtualTourRepository;
    private final SiteRepository siteRepository;

    public List<VirtualTour> getToursBySite(Long siteId) {
        return virtualTourRepository.findBySite_SiteId(siteId);
    }

    public VirtualTour getTourById(Long id) {
        return virtualTourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("VirtualTour not found"));
    }

    public VirtualTour createTour(Long siteId, VirtualTour tour) {
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new RuntimeException("Site not found"));
        tour.setSite(site);
        return virtualTourRepository.save(tour);
    }

    public VirtualTour updateTour(Long id, VirtualTour updated) {
        VirtualTour existing = getTourById(id);
        existing.setTitle(updated.getTitle());
        return virtualTourRepository.save(existing);
    }

    public void deleteTour(Long id) {
        virtualTourRepository.deleteById(id);
    }
}