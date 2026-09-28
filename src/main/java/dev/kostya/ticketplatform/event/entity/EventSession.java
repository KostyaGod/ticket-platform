package dev.kostya.ticketplatform.event.entity;

import dev.kostya.ticketplatform.event.enums.EventSessionStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_sessions")
public class EventSession {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_session_seq")
    @SequenceGenerator(
            name = "event_session_seq",
            sequenceName = "event_session_seq",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EventSessionStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "sales_start_at", nullable = false)
    private LocalDateTime salesStartAt;

    @Column(name = "sales_end_at", nullable = false)
    private LocalDateTime salesEndAt;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;
}
