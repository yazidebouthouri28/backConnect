package tn.esprit.projetintegre.dto.response;

import lombok.*;
import tn.esprit.projetintegre.enums.CriterieName;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CertificationItemResponse {
    private Long id;
    private String name;
    private String description;
    private Integer score;
    private Integer requiredScore;
    private Boolean passed;
    private LocalDateTime completedAt;
    private CriterieName criteriaName;
    private String comment;
    private Long certificationId;
}
