package dev.kostya.ticketplatform.event.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@Getter @Setter
@NoArgsConstructor
public class Event extends SoftDeletableEntity implements IdentifiableAndNamed {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_seq")
    @SequenceGenerator(
            name = "event_seq",
            sequenceName = "event_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer duration;

    @Column(length = 100)
    private String category;

    @Column(name = "age_limit", columnDefinition = "INT CHECK (age_limit >= 0 AND age_limit <= 150)")
    private Integer ageLimit;

    private String organizer;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @OneToMany(mappedBy = "event")
    private List<EventSession> sessions = new ArrayList<>();

    public void addSession(EventSession session) {
        sessions.add(session);
        session.setEvent(this);
    }

    public void removeSession(EventSession session) {
        sessions.remove(session);
        session.setEvent(null);
    }
}
