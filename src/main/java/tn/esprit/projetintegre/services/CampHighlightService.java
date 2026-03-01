package tn.esprit.projetintegre.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.projetintegre.dto.request.CampHighlightRequest;
import tn.esprit.projetintegre.dto.response.CampHighlightResponse;
import tn.esprit.projetintegre.entities.CampHighlight;
import tn.esprit.projetintegre.entities.Site;
import tn.esprit.projetintegre.enums.HighlightCategory;
import tn.esprit.projetintegre.mapper.SiteModuleMapper;
import tn.esprit.projetintegre.repositories.CampHighlightRepository;
import tn.esprit.projetintegre.repositories.SiteRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CampHighlightService {

    private final CampHighlightRepository campHighlightRepository;
    private final SiteRepository siteRepository;
    private final SiteModuleMapper siteMapper;

    public List<CampHighlightResponse> getHighlightsBySite(Long siteId) {
        return siteMapper.toCampHighlightResponseList(campHighlightRepository.findBySite_Id(siteId));
    }

    public List<CampHighlightResponse> getHighlightsBySiteAndCategory(Long siteId, HighlightCategory category) {
        return siteMapper
                .toCampHighlightResponseList(campHighlightRepository.findBySite_IdAndCategory(siteId, category));
    }

    public CampHighlightResponse getHighlightById(Long id) {
        CampHighlight highlight = campHighlightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Highlight not found"));
        return siteMapper.toResponse(highlight);
    }

    public CampHighlightResponse createHighlight(Long siteId, CampHighlightRequest request) {
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new RuntimeException("Site not found"));
        CampHighlight highlight = siteMapper.toEntity(request, site);
        return siteMapper.toResponse(campHighlightRepository.save(highlight));
    }

    public CampHighlightResponse updateHighlight(Long id, CampHighlightRequest request) {
        CampHighlight existing = campHighlightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Highlight not found"));
        siteMapper.updateEntity(existing, request);
        return siteMapper.toResponse(campHighlightRepository.save(existing));
    }

    public void deleteHighlight(Long id) {
        campHighlightRepository.deleteById(id);
    }
}