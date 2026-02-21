package com.camping.projet.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventServiceRequest {
    @NotNull
    private Long eventId;

    @NotNull
    private Long serviceId;

    @NotNull
    @Min(1)
    private Integer requiredQuantity;
}
