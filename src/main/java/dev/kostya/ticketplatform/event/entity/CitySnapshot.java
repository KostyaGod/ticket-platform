package dev.kostya.ticketplatform.event.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import java.util.List;

@Entity
@Table(name = "city_snapshot")
@Getter @Setter
@NoArgsConstructor
public class CitySnapshot implements Persistable<Long>, IdentifiableAndNamed {

    @Id
    @Column(name = "city_id")
    private Long id;

    @Column(name = "city_name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "city")
    private List<VenueSnapshot> venues;

    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }

    public CitySnapshot(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addVenue(VenueSnapshot venue) {
        venues.add(venue);
        venue.setCity(this);
    }

    public void removeVenue(VenueSnapshot venue) {
        venues.remove(venue);
        venue.setCity(null);
    }

    @PostLoad
    @PostPersist
    void markNotNew() {
        this.isNew = false;
    }
}
