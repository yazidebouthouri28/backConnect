package tn.esprit.projetintegre.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.projetintegre.entities.Site;
import tn.esprit.projetintegre.repositories.SiteRepository;


import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;

    public List<Site> getAllSites() {
        return siteRepository.findAll();
    }

    public Site getSiteById(Long id) {
        return siteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Site not found with id: " + id));
    }

    public Site createSite(Site site) {
        return siteRepository.save(site);
    }

    public Site updateSite(Long id, Site updated) {
        Site existing = getSiteById(id);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setLatitude(updated.getLatitude());
        existing.setLongitude(updated.getLongitude());
        existing.setAddress(updated.getAddress());
        existing.setCity(updated.getCity());
        existing.setImage(updated.getImage());
        return siteRepository.save(existing);
    }

    public void deleteSite(Long id) {
        siteRepository.deleteById(id);
    }

    public List<Site> getSitesByCity(String city) {
        return siteRepository.findByCityIgnoreCase(city);
    }

    public List<Site> searchSitesByName(String name) {
        return siteRepository.findByNameContainingIgnoreCase(name);
    }
}