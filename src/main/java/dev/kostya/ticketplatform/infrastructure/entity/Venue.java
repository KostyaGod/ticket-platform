package dev.kostya.ticketplatform.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="venues")
@Getter @Setter
@NoArgsConstructor
public class Venue extends SoftDeletableEntity implements IdentifiableAndNamed {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "venue_seq")
    @SequenceGenerator(
            name = "venue_seq",
            sequenceName = "venue_seq",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "venue")
    private List<Hall> halls = new ArrayList<>();

    public Long getCityId() {
        return city.getId();
    }

    public void addHall(Hall hall) {
        halls.add(hall);
        hall.setVenue(this);
    }

    public void removeHall(Hall hall) {
        halls.remove(hall);
        hall.setVenue(null);
    }
}
