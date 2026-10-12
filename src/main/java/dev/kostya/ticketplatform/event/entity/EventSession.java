package dev.kostya.ticketplatform.event.entity;

import dev.kostya.ticketplatform.event.enums.EventSessionStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_sessions")
public class EventSession extends SoftDeletableEntity {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "event_session_seq"
    )
    @SequenceGenerator(
            name = "event_session_seq",
            sequenceName = "event_session_seq",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "hall_id", nullable = false)
    private Long hallId;

    @Column(name = "venue_id", nullable = false)
    private Long venueId;

    @Column(name = "city_id", nullable = false)
    private Long cityId;

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EventSessionStatus status;

    @Column(name = "sales_start_at", nullable = false)
    private Instant salesStartAt;

    @Column(name = "sales_end_at", nullable = false)
    private Instant salesEndAt;

    @Column(name = "start_at", nullable = false)
    private Instant startAt;

    @Column(name = "end_at", nullable = false)
    private Instant endAt;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
