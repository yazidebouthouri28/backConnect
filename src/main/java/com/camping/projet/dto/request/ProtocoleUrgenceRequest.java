package com.camping.projet.dto.request;

import com.camping.projet.enums.TypeUrgence;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProtocoleUrgenceRequest {
    @NotBlank
    @Size(min = 3, max = 100)
    private String nom;

    @NotNull
    private TypeUrgence type;

    private List<String> etapes;
    private Map<String, String> contactsUrgence;

    @Min(1)
    @Max(5)
    private int gravite;
}
