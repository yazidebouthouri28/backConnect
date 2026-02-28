package tn.esprit.projetintegre.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.projetintegre.entities.CampHighlight;
import tn.esprit.projetintegre.entities.Site;
import tn.esprit.projetintegre.enums.HighlightCategory;
import tn.esprit.projetintegre.repositories.CampHighlightRepository;
import tn.esprit.projetintegre.repositories.SiteRepository;


import java.util.List;

@Service
@RequiredArgsConstructor
public class CampHighlightService {

    private final CampHighlightRepository campHighlightRepository;
    private final SiteRepository siteRepository;

    public List<CampHighlight> getHighlightsBySite(Long siteId) {
        return campHighlightRepository.findBySite_SiteId(siteId);
    }

    public List<CampHighlight> getHighlightsBySiteAndCategory(Long siteId, HighlightCategory category) {
        return campHighlightRepository.findBySite_SiteIdAndCategory(siteId, category);
    }

    public CampHighlight getHighlightById(Long id) {
        return campHighlightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Highlight not found"));
    }

    public CampHighlight createHighlight(Long siteId, CampHighlight highlight) {
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new RuntimeException("Site not found"));
        highlight.setSite(site);
        return campHighlightRepository.save(highlight);
    }

    public CampHighlight updateHighlight(Long id, CampHighlight updated) {
        CampHighlight existing = getHighlightById(id);
        existing.setTitle(updated.getTitle());
        existing.setContent(updated.getContent());
        existing.setCategory(updated.getCategory());
        existing.setImageUrl(updated.getImageUrl());
        return campHighlightRepository.save(existing);
    }

    public void deleteHighlight(Long id) {
        campHighlightRepository.deleteById(id);
    }
}