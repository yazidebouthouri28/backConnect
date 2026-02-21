package com.camping.projet.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidatureServiceRequest {
    @NotNull
    private Long userId;

    @NotNull
    private Long eventServiceId;

    @Size(max = 500)
    private String message;

    @Size(max = 300)
    private String skills;
}
