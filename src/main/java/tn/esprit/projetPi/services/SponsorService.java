package tn.esprit.projetPi.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.projetPi.dto.SponsorDTO;
import tn.esprit.projetPi.entities.Sponsor;
import tn.esprit.projetPi.entities.SponsorTier;
import tn.esprit.projetPi.exception.ResourceNotFoundException;
import tn.esprit.projetPi.repositories.SponsorRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SponsorService {

    private final SponsorRepository sponsorRepository;

    public List<SponsorDTO> getAllSponsors() {
        return sponsorRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<SponsorDTO> getActiveSponsors() {
        return sponsorRepository.findByIsActiveTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<SponsorDTO> getSponsorsByTier(SponsorTier tier) {
        return sponsorRepository.findByTier(tier).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<SponsorDTO> getSponsorsForEvent(String eventId) {
        return sponsorRepository.findByEventId(eventId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public SponsorDTO getSponsorById(String id) {
        Sponsor sponsor = sponsorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sponsor not found with id: " + id));
        return toDTO(sponsor);
    }

    public SponsorDTO createSponsor(SponsorDTO dto) {
        Sponsor sponsor = toEntity(dto);
        sponsor.setCreatedAt(LocalDateTime.now());
        sponsor.setUpdatedAt(LocalDateTime.now());
        sponsor.setIsActive(true);
        return toDTO(sponsorRepository.save(sponsor));
    }

    public SponsorDTO updateSponsor(String id, SponsorDTO dto) {
        Sponsor sponsor = sponsorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sponsor not found with id: " + id));
        
        updateSponsorFromDTO(sponsor, dto);
        sponsor.setUpdatedAt(LocalDateTime.now());
        return toDTO(sponsorRepository.save(sponsor));
    }

    public void deleteSponsor(String id) {
        if (!sponsorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sponsor not found with id: " + id);
        }
        sponsorRepository.deleteById(id);
    }

    public SponsorDTO toggleActive(String id) {
        Sponsor sponsor = sponsorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sponsor not found with id: " + id));
        sponsor.setIsActive(!Boolean.TRUE.equals(sponsor.getIsActive()));
        sponsor.setUpdatedAt(LocalDateTime.now());
        return toDTO(sponsorRepository.save(sponsor));
    }

    public SponsorDTO addEventToSponsor(String sponsorId, String eventId) {
        Sponsor sponsor = sponsorRepository.findById(sponsorId)
                .orElseThrow(() -> new ResourceNotFoundException("Sponsor not found with id: " + sponsorId));
        
        if (sponsor.getSponsoredEventIds() != null && !sponsor.getSponsoredEventIds().contains(eventId)) {
            sponsor.getSponsoredEventIds().add(eventId);
            sponsor.setUpdatedAt(LocalDateTime.now());
            sponsorRepository.save(sponsor);
        }
        return toDTO(sponsor);
    }

    public List<SponsorDTO> searchSponsors(String query) {
        return sponsorRepository.searchByName(query).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private SponsorDTO toDTO(Sponsor sponsor) {
        return SponsorDTO.builder()
                .id(sponsor.getId())
                .name(sponsor.getName())
                .description(sponsor.getDescription())
                .logo(sponsor.getLogo())
                .website(sponsor.getWebsite())
                .email(sponsor.getEmail())
                .phone(sponsor.getPhone())
                .contactName(sponsor.getContactName())
                .contactEmail(sponsor.getContactEmail())
                .contactPhone(sponsor.getContactPhone())
                .tier(sponsor.getTier())
                .sponsorshipAmount(sponsor.getSponsorshipAmount())
                .currency(sponsor.getCurrency())
                .startDate(sponsor.getStartDate())
                .endDate(sponsor.getEndDate())
                .isActive(sponsor.getIsActive())
                .sponsoredEventIds(sponsor.getSponsoredEventIds())
                .sponsoredEventsCount(sponsor.getSponsoredEventIds() != null ? sponsor.getSponsoredEventIds().size() : 0)
                .benefits(sponsor.getBenefits())
                .socialLinks(sponsor.getSocialLinks())
                .banners(sponsor.getBanners())
                .promotionalMaterials(sponsor.getPromotionalMaterials())
                .createdAt(sponsor.getCreatedAt())
                .updatedAt(sponsor.getUpdatedAt())
                .build();
    }

    private Sponsor toEntity(SponsorDTO dto) {
        Sponsor sponsor = new Sponsor();
        updateSponsorFromDTO(sponsor, dto);
        return sponsor;
    }

    private void updateSponsorFromDTO(Sponsor sponsor, SponsorDTO dto) {
        if (dto.getName() != null) sponsor.setName(dto.getName());
        if (dto.getDescription() != null) sponsor.setDescription(dto.getDescription());
        if (dto.getLogo() != null) sponsor.setLogo(dto.getLogo());
        if (dto.getWebsite() != null) sponsor.setWebsite(dto.getWebsite());
        if (dto.getEmail() != null) sponsor.setEmail(dto.getEmail());
        if (dto.getPhone() != null) sponsor.setPhone(dto.getPhone());
        if (dto.getContactName() != null) sponsor.setContactName(dto.getContactName());
        if (dto.getContactEmail() != null) sponsor.setContactEmail(dto.getContactEmail());
        if (dto.getContactPhone() != null) sponsor.setContactPhone(dto.getContactPhone());
        if (dto.getTier() != null) sponsor.setTier(dto.getTier());
        if (dto.getSponsorshipAmount() != null) sponsor.setSponsorshipAmount(dto.getSponsorshipAmount());
        if (dto.getCurrency() != null) sponsor.setCurrency(dto.getCurrency());
        if (dto.getStartDate() != null) sponsor.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) sponsor.setEndDate(dto.getEndDate());
        if (dto.getIsActive() != null) sponsor.setIsActive(dto.getIsActive());
        if (dto.getSponsoredEventIds() != null) sponsor.setSponsoredEventIds(dto.getSponsoredEventIds());
        if (dto.getBenefits() != null) sponsor.setBenefits(dto.getBenefits());
        if (dto.getSocialLinks() != null) sponsor.setSocialLinks(dto.getSocialLinks());
        if (dto.getBanners() != null) sponsor.setBanners(dto.getBanners());
        if (dto.getPromotionalMaterials() != null) sponsor.setPromotionalMaterials(dto.getPromotionalMaterials());
    }
}
