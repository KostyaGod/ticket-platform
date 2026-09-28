package dev.kostya.ticketplatform.event.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@Getter @Setter
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_seq")
    @SequenceGenerator(
            name = "event_seq",
            sequenceName = "event_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    @Column(length = 100)
    private String category;

    @Column(name = "age_limit")
    private Integer ageLimit;

    private String organizer;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "event")
    private List<EventSession> sessions = new ArrayList<>();
}
