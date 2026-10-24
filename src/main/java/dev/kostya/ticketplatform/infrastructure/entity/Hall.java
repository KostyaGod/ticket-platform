package dev.kostya.ticketplatform.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "halls")
@Getter @Setter
@NoArgsConstructor
public class Hall extends SoftDeletableEntity implements IdentifiableAndNamed {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hall_seq")
    @SequenceGenerator(
            name = "hall_seq",
            sequenceName = "hall_seq",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "hall")
    private List<Seat> seats = new ArrayList<>();

    public Long getVenueId() {
        return venue.getId();
    }

    public void addSeat(Seat seat) {
        this.seats.add(seat);
        seat.setHall(this);
    }

    public void removeSeat(Seat seat) {
        this.seats.remove(seat);
        seat.setHall(null);
    }
}
