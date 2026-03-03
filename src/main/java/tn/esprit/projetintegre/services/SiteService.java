package tn.esprit.projetintegre.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.projetintegre.dto.request.SiteRequest;
import tn.esprit.projetintegre.dto.response.SiteResponse;
import tn.esprit.projetintegre.dto.response.SiteSummaryResponse;
import tn.esprit.projetintegre.entities.Site;
import tn.esprit.projetintegre.mapper.SiteModuleMapper;
import tn.esprit.projetintegre.repositories.SiteRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;
    private final SiteModuleMapper siteMapper;
    private final SiteImageStorageService siteImageStorageService;

    public List<SiteResponse> getAllSites() {
        return siteMapper.toSiteResponseList(siteRepository.findAll());
    }

    public List<SiteSummaryResponse> getSiteSummaries() {
        return siteMapper.toSiteSummaryResponseList(siteRepository.findAll());
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

    public SiteResponse addSiteImages(Long id, List<MultipartFile> files) {
        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Site not found with id: " + id));

        List<String> urls = siteImageStorageService.storeSiteImages(site.getId(), files);
        if (urls.isEmpty()) {
            return siteMapper.toResponse(site);
        }

        List<String> images = site.getImages() != null ? site.getImages() : new ArrayList<>();
        images.addAll(urls);
        site.setImages(images);
        site.setThumbnail(images.isEmpty() ? null : images.get(0));

        return siteMapper.toResponse(siteRepository.save(site));
    }

    public SiteResponse removeSiteImage(Long id, String url) {
        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Site not found with id: " + id));

        List<String> images = site.getImages();
        if (images == null || images.isEmpty()) {
            return siteMapper.toResponse(site);
        }

        boolean removed = images.removeIf(img -> img != null && img.equals(url));
        if (removed) {
            siteImageStorageService.deleteByPublicUrl(url);
            site.setImages(images);
            site.setThumbnail(images.isEmpty() ? null : images.get(0));
            site = siteRepository.save(site);
        }

        return siteMapper.toResponse(site);
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
