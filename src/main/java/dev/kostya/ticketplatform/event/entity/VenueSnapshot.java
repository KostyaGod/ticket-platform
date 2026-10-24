package dev.kostya.ticketplatform.event.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import java.util.List;

@Entity
@Table(name = "venue_snapshot")
@Getter @Setter
@NoArgsConstructor
public class VenueSnapshot implements Persistable<Long>, IdentifiableAndNamed {
    @Id
    @Column(name = "venue_id")
    private Long id;

    @Column(name = "venue_name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "city_id", nullable = false)
    private CitySnapshot city;

    @OneToMany(mappedBy = "venue")
    private List<HallSnapshot> halls;

    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }

    public VenueSnapshot(Long id, String name, CitySnapshot city) {
        this.id = id;
        this.name = name;
        this.city = city;
    }

    public Long getCityId() {
        return city.getId();
    }

    public void addHall(HallSnapshot hall) {
        halls.add(hall);
        hall.setVenue(this);
    }

    public void removeHall(HallSnapshot hall) {
        halls.remove(hall);
        hall.setVenue(null);
    }

    @PostLoad
    @PostPersist
    void markNotNew() {
        this.isNew = false;
    }
}
