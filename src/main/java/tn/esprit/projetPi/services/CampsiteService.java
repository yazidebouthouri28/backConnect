package tn.esprit.projetPi.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.projetPi.dto.CampsiteDTO;
import tn.esprit.projetPi.dto.CampsiteSearchRequest;
import tn.esprit.projetPi.entities.Campsite;
import tn.esprit.projetPi.exception.ResourceNotFoundException;
import tn.esprit.projetPi.repositories.CampsiteRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampsiteService {

    private final CampsiteRepository campsiteRepository;

    public List<CampsiteDTO> getAllCampsites() {
        return campsiteRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<CampsiteDTO> getActiveCampsites() {
        return campsiteRepository.findByIsActiveTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<CampsiteDTO> getFeaturedCampsites() {
        return campsiteRepository.findByIsFeaturedTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CampsiteDTO getCampsiteById(String id) {
        Campsite campsite = campsiteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campsite not found with id: " + id));
        return toDTO(campsite);
    }

    public List<CampsiteDTO> getCampsitesByOwner(String ownerId) {
        return campsiteRepository.findByOwnerId(ownerId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CampsiteDTO createCampsite(CampsiteDTO dto, String ownerId, String ownerName) {
        Campsite campsite = toEntity(dto);
        campsite.setOwnerId(ownerId);
        campsite.setOwnerName(ownerName);
        campsite.setCreatedAt(LocalDateTime.now());
        campsite.setUpdatedAt(LocalDateTime.now());
        campsite.setIsActive(true);
        campsite.setAverageRating(0.0);
        campsite.setReviewCount(0);
        return toDTO(campsiteRepository.save(campsite));
    }

    public CampsiteDTO updateCampsite(String id, CampsiteDTO dto) {
        Campsite campsite = campsiteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campsite not found with id: " + id));
        
        updateCampsiteFromDTO(campsite, dto);
        campsite.setUpdatedAt(LocalDateTime.now());
        return toDTO(campsiteRepository.save(campsite));
    }

    public void deleteCampsite(String id) {
        if (!campsiteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Campsite not found with id: " + id);
        }
        campsiteRepository.deleteById(id);
    }

    public List<CampsiteDTO> searchCampsites(CampsiteSearchRequest request) {
        List<Campsite> results;
        
        if (request.getQuery() != null && !request.getQuery().isEmpty()) {
            results = campsiteRepository.searchCampsites(request.getQuery());
        } else if (request.getCity() != null) {
            results = campsiteRepository.findByCity(request.getCity());
        } else if (request.getAmenities() != null && !request.getAmenities().isEmpty()) {
            results = campsiteRepository.findByAmenitiesContainingAny(request.getAmenities());
        } else {
            results = campsiteRepository.findByIsActiveTrue();
        }
        
        // Apply filters
        if (request.getMinPrice() != null || request.getMaxPrice() != null) {
            Double minPrice = request.getMinPrice() != null ? request.getMinPrice() : 0.0;
            Double maxPrice = request.getMaxPrice() != null ? request.getMaxPrice() : Double.MAX_VALUE;
            results = results.stream()
                    .filter(c -> c.getPricePerNight() >= minPrice && c.getPricePerNight() <= maxPrice)
                    .collect(Collectors.toList());
        }
        
        if (request.getGuests() != null) {
            results = results.stream()
                    .filter(c -> c.getMaxGuests() >= request.getGuests())
                    .collect(Collectors.toList());
        }
        
        if (request.getMinRating() != null) {
            results = results.stream()
                    .filter(c -> c.getAverageRating() != null && c.getAverageRating() >= request.getMinRating())
                    .collect(Collectors.toList());
        }
        
        return results.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<CampsiteDTO> findByLocationBounds(Double minLat, Double maxLat, Double minLng, Double maxLng) {
        return campsiteRepository.findByLocationBounds(minLat, maxLat, minLng, maxLng).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CampsiteDTO toggleFeatured(String id) {
        Campsite campsite = campsiteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campsite not found with id: " + id));
        campsite.setIsFeatured(!Boolean.TRUE.equals(campsite.getIsFeatured()));
        campsite.setUpdatedAt(LocalDateTime.now());
        return toDTO(campsiteRepository.save(campsite));
    }

    public CampsiteDTO toggleActive(String id) {
        Campsite campsite = campsiteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campsite not found with id: " + id));
        campsite.setIsActive(!Boolean.TRUE.equals(campsite.getIsActive()));
        campsite.setUpdatedAt(LocalDateTime.now());
        return toDTO(campsiteRepository.save(campsite));
    }

    public void updateRating(String campsiteId, Double newRating, Integer newReviewCount) {
        Campsite campsite = campsiteRepository.findById(campsiteId)
                .orElseThrow(() -> new ResourceNotFoundException("Campsite not found with id: " + campsiteId));
        campsite.setAverageRating(newRating);
        campsite.setReviewCount(newReviewCount);
        campsiteRepository.save(campsite);
    }

    private CampsiteDTO toDTO(Campsite campsite) {
        return CampsiteDTO.builder()
                .id(campsite.getId())
                .name(campsite.getName())
                .description(campsite.getDescription())
                .shortDescription(campsite.getShortDescription())
                .address(campsite.getAddress())
                .city(campsite.getCity())
                .state(campsite.getState())
                .country(campsite.getCountry())
                .zipCode(campsite.getZipCode())
                .latitude(campsite.getLatitude())
                .longitude(campsite.getLongitude())
                .pricePerNight(campsite.getPricePerNight())
                .cleaningFee(campsite.getCleaningFee())
                .serviceFee(campsite.getServiceFee())
                .currency(campsite.getCurrency())
                .amenities(campsite.getAmenities())
                .activities(campsite.getActivities())
                .rules(campsite.getRules())
                .maxGuests(campsite.getMaxGuests())
                .tents(campsite.getTents())
                .cabins(campsite.getCabins())
                .rvSpots(campsite.getRvSpots())
                .images(campsite.getImages())
                .featuredImage(campsite.getFeaturedImage())
                .isActive(campsite.getIsActive())
                .isFeatured(campsite.getIsFeatured())
                .instantBooking(campsite.getInstantBooking())
                .averageRating(campsite.getAverageRating())
                .reviewCount(campsite.getReviewCount())
                .ownerId(campsite.getOwnerId())
                .ownerName(campsite.getOwnerName())
                .phone(campsite.getPhone())
                .email(campsite.getEmail())
                .website(campsite.getWebsite())
                .blockedDates(campsite.getBlockedDates())
                .minNights(campsite.getMinNights())
                .maxNights(campsite.getMaxNights())
                .createdAt(campsite.getCreatedAt())
                .updatedAt(campsite.getUpdatedAt())
                .build();
    }

    private Campsite toEntity(CampsiteDTO dto) {
        Campsite campsite = new Campsite();
        updateCampsiteFromDTO(campsite, dto);
        return campsite;
    }

    private void updateCampsiteFromDTO(Campsite campsite, CampsiteDTO dto) {
        if (dto.getName() != null) campsite.setName(dto.getName());
        if (dto.getDescription() != null) campsite.setDescription(dto.getDescription());
        if (dto.getShortDescription() != null) campsite.setShortDescription(dto.getShortDescription());
        if (dto.getAddress() != null) campsite.setAddress(dto.getAddress());
        if (dto.getCity() != null) campsite.setCity(dto.getCity());
        if (dto.getState() != null) campsite.setState(dto.getState());
        if (dto.getCountry() != null) campsite.setCountry(dto.getCountry());
        if (dto.getZipCode() != null) campsite.setZipCode(dto.getZipCode());
        if (dto.getLatitude() != null) campsite.setLatitude(dto.getLatitude());
        if (dto.getLongitude() != null) campsite.setLongitude(dto.getLongitude());
        if (dto.getPricePerNight() != null) campsite.setPricePerNight(dto.getPricePerNight());
        if (dto.getCleaningFee() != null) campsite.setCleaningFee(dto.getCleaningFee());
        if (dto.getServiceFee() != null) campsite.setServiceFee(dto.getServiceFee());
        if (dto.getCurrency() != null) campsite.setCurrency(dto.getCurrency());
        if (dto.getAmenities() != null) campsite.setAmenities(dto.getAmenities());
        if (dto.getActivities() != null) campsite.setActivities(dto.getActivities());
        if (dto.getRules() != null) campsite.setRules(dto.getRules());
        if (dto.getMaxGuests() != null) campsite.setMaxGuests(dto.getMaxGuests());
        if (dto.getTents() != null) campsite.setTents(dto.getTents());
        if (dto.getCabins() != null) campsite.setCabins(dto.getCabins());
        if (dto.getRvSpots() != null) campsite.setRvSpots(dto.getRvSpots());
        if (dto.getImages() != null) campsite.setImages(dto.getImages());
        if (dto.getFeaturedImage() != null) campsite.setFeaturedImage(dto.getFeaturedImage());
        if (dto.getIsActive() != null) campsite.setIsActive(dto.getIsActive());
        if (dto.getIsFeatured() != null) campsite.setIsFeatured(dto.getIsFeatured());
        if (dto.getInstantBooking() != null) campsite.setInstantBooking(dto.getInstantBooking());
        if (dto.getPhone() != null) campsite.setPhone(dto.getPhone());
        if (dto.getEmail() != null) campsite.setEmail(dto.getEmail());
        if (dto.getWebsite() != null) campsite.setWebsite(dto.getWebsite());
        if (dto.getBlockedDates() != null) campsite.setBlockedDates(dto.getBlockedDates());
        if (dto.getMinNights() != null) campsite.setMinNights(dto.getMinNights());
        if (dto.getMaxNights() != null) campsite.setMaxNights(dto.getMaxNights());
    }
}
