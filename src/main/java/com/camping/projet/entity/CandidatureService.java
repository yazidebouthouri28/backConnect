package com.camping.projet.entity;

import com.camping.projet.enums.ApplicationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidatures_service", uniqueConstraints = {
        @UniqueConstraint(name = "uc_user_event_service", columnNames = { "user_id", "event_service_id" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidatureService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_candidature_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_service_id", nullable = false, foreignKey = @ForeignKey(name = "fk_candidature_event_service"))
    private EventService eventService;

    @Size(max = 500)
    private String message;

    @Size(max = 300)
    private String skills;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @Column(updatable = false)
    private LocalDateTime applicationDate;

    @PrePersist
    protected void onCreate() {
        applicationDate = LocalDateTime.now();
        if (status == null) {
            status = ApplicationStatus.PENDING;
        }
    }

    // Business Method
    public boolean canBeWithdrawn() {
        return status == ApplicationStatus.PENDING;
    }
}
