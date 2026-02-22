package tn.esprit.projetPi.dto;

import lombok.*;
import tn.esprit.projetPi.entities.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventSearchRequest {
    private String query;
    private EventType type;
    private String city;
    private String state;
    private String country;
    private LocalDateTime startDateFrom;
    private LocalDateTime startDateTo;
    private Double maxPrice;
    private Boolean isFree;
    private Boolean isVirtual;
    private List<String> tags;
    private List<String> categories;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}
