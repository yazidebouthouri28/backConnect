package tn.esprit.projetintegre.dto.response;

import lombok.*;
import tn.esprit.projetintegre.enums.ServiceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventServiceEntityResponse {
    private Long id;
    private String name;
    private String description;
    private ServiceType serviceType;
    private BigDecimal price;
    private Boolean included;
    private Boolean optional;
    private Integer quantity;
    private Integer quantiteRequise;
    private Integer quantiteAcceptee;
    private String notes;
    private Long eventId;
    private String eventTitle;
    private Long serviceId;
    private String serviceName;
    private Long providerId;
    private String providerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
