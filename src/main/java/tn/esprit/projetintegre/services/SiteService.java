package tn.esprit.projetintegre.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.projetintegre.dto.request.SiteRequest;
import tn.esprit.projetintegre.dto.response.SiteResponse;
import tn.esprit.projetintegre.entities.Site;
import tn.esprit.projetintegre.mapper.SiteModuleMapper;
import tn.esprit.projetintegre.repositories.SiteRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;
    private final SiteModuleMapper siteMapper;

    public List<SiteResponse> getAllSites() {
        return siteMapper.toSiteResponseList(siteRepository.findAll());
    }

    public SiteResponse getSiteById(Long id) {
        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Site not found with id: " + id));
        return siteMapper.toResponse(site);
    }

    public SiteResponse createSite(SiteRequest request) {
        Site site = siteMapper.toEntity(request);
        return siteMapper.toResponse(siteRepository.save(site));
    }

    public SiteResponse updateSite(Long id, SiteRequest request) {
        Site existing = siteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Site not found with id: " + id));
        siteMapper.updateEntity(existing, request);
        return siteMapper.toResponse(siteRepository.save(existing));
    }

    public void deleteSite(Long id) {
        siteRepository.deleteById(id);
    }

    public List<SiteResponse> getSitesByCity(String city) {
        return siteMapper.toSiteResponseList(siteRepository.findByCityIgnoreCase(city));
    }

    public List<SiteResponse> searchSitesByName(String name) {
        return siteMapper.toSiteResponseList(siteRepository.findByNameContainingIgnoreCase(name));
    }
}