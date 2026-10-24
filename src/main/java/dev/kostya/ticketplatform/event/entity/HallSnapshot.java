package dev.kostya.ticketplatform.event.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "hall_snapshot")
@Getter @Setter
@NoArgsConstructor
public class HallSnapshot implements Persistable<Long>, IdentifiableAndNamed {
    @Id
    @Column(name = "hall_id")
    private Long id;

    @Column(name = "hall_name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venue_id", nullable = false)
    private VenueSnapshot venue;

    @Transient
    private boolean isNew;

    public HallSnapshot(Long id, String name, VenueSnapshot venue) {
        this.id = id;
        this.name = name;
        this.venue = venue;
    }

    public Long getVenueId() {
        return venue.getId();
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostLoad
    @PostPersist
    void markNotNew() {
        this.isNew = false;
    }
}
