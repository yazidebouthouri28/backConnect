package tn.esprit.projetPi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WishlistDTO {
    private String id;
    private String userId;
    private String name;
    private Boolean isDefault;
    private Boolean isPublic;
    private List<WishlistItemDTO> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
