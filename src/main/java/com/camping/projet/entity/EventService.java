package com.camping.projet.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "event_services", uniqueConstraints = {
        @UniqueConstraint(name = "uc_event_service", columnNames = { "eventId", "service_id" })
}, indexes = {
        @Index(name = "idx_event_service_event", columnList = "eventId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Long eventId; // FK to Event (Teammate's entity)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false, foreignKey = @ForeignKey(name = "fk_event_service_service"))
    private Service service;

    @Min(1)
    @Column(nullable = false)
    private Integer requiredQuantity;

    @Min(0)
    @Column(nullable = false)
    private Integer acceptedQuantity;

    @PrePersist
    protected void onCreate() {
        if (acceptedQuantity == null) {
            acceptedQuantity = 0;
        }
    }

    // Business Methods
    public boolean hasAvailableSpots() {
        return acceptedQuantity < requiredQuantity;
    }

    public void incrementAccepted() {
        if (hasAvailableSpots()) {
            this.acceptedQuantity++;
        } else {
            throw new IllegalStateException("Maximum quantity reached for this service in the event.");
        }
    }

    public void decrementAccepted() {
        if (this.acceptedQuantity > 0) {
            this.acceptedQuantity--;
        }
    }
}
