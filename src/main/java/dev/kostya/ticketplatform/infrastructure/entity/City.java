package dev.kostya.ticketplatform.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "cities")
@Getter @Setter
@NoArgsConstructor
public class City extends SoftDeletableEntity implements IdentifiableAndNamed {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "city_seq")
    @SequenceGenerator(
            name = "city_seq",
            sequenceName = "city_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 64)
    private String timezone;

    @OneToMany(mappedBy = "city")
    private List<Venue> venues;

    public void addVenue(Venue venue) {
        venues.add(venue);
        venue.setCity(this);
    }

    public void removeVenue(Venue venue) {
        venues.remove(venue);
        venue.setCity(null);
    }
}
