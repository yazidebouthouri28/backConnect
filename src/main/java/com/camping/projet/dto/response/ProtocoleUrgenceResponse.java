package com.camping.projet.dto.response;

import com.camping.projet.enums.TypeUrgence;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProtocoleUrgenceResponse {
    private Long id;
    private String nom;
    private TypeUrgence type;
    private List<String> etapes;
    private Map<String, String> contactsUrgence;
    private int gravite;
    private LocalDateTime derniereRevision;
}
