package tn.esprit.backconnect.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.backconnect.dto.*;
import tn.esprit.backconnect.entities.*;
import tn.esprit.backconnect.dto.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DtoMapper {
    // Sponsor Mapping
    public SponsorResponse toSponsorResponse(Sponsor entity) {
        if (entity == null) return null;
        return SponsorResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .logo(entity.getLogo())
                .website(entity.getWebsite())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                // Note: Assurez-vous que ces champs existent dans l'entité Sponsor
                .address(null)
                .city(null)
                .country(null)
                .contactPerson(entity.getContactPerson())
                .isActive(entity.getIsActive())
                .sponsorshipCount(entity.getSponsorships() != null ? entity.getSponsorships().size() : 0)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // Sponsorship Mapping
    public SponsorshipResponse toSponsorshipResponse(Sponsorship entity) {
        if (entity == null) return null;
        return SponsorshipResponse.builder()
                .id(entity.getId())
                .sponsorId(entity.getSponsor() != null ? entity.getSponsor().getId() : null)
                .sponsorName(entity.getSponsor() != null ? entity.getSponsor().getName() : null)
                .eventId(entity.getEvent() != null ? entity.getEvent().getId() : null)
                .eventTitle(entity.getEvent() != null ? entity.getEvent().getTitle() : null)
                .sponsorshipType(entity.getSponsorshipType())
                .sponsorshipLevel(entity.getSponsorshipLevel())
                .description(entity.getDescription())
                .amount(entity.getAmount())
                .currency(entity.getCurrency())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .isPaid(entity.getIsPaid())
                .paidAt(entity.getPaidAt())
                .benefits(entity.getBenefits())
                .deliverables(entity.getDeliverables())
                .isActive(entity.getIsActive())
                .status(entity.getStatus())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public List<SponsorshipResponse> toSponsorshipResponseList(List<Sponsorship> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream().map(this::toSponsorshipResponse).collect(Collectors.toList());
    }



    // Sponsor Mapping
    public List<SponsorResponse> toSponsorResponseList(List<Sponsor> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream().map(this::toSponsorResponse).collect(Collectors.toList());
    }
    public List<OrderResponse> toOrderResponseList(List<Order> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream().map(this::toOrderResponse).collect(Collectors.toList());
    }

}