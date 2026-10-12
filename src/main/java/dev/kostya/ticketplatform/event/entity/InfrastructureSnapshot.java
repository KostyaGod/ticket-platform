package dev.kostya.ticketplatform.event.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "infrastructure_snapshot")
public class InfrastructureSnapshot {
    @Id
    @Column(name = "event_session_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "event_session_id", nullable = false)
    private EventSession eventSession;

    @Column(name = "city_id", nullable = false)
    private Long cityId;

    @Column(name = "hall_id", nullable = false)
    private Long hallId;

    @Column(name = "hall_name", nullable = false)
    private String hallName;

    @Column(name = "venue_id", nullable = false)
    private Long venueId;

    @Column(name = "venue_name", nullable = false)
    private String venueName;

    @Column(name = "snapshot_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime snapshotAt;
}
