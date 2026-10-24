package dev.kostya.ticketplatform.infrastructure.repositories;

import dev.kostya.ticketplatform.infrastructure.entity.Venue;
import dev.kostya.ticketplatform.infrastructure.sync.projection.VenueSyncProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VenueRepository extends JpaRepository<Venue, Long> {

    @Query("""
        SELECT v.id AS id,
                v.name AS name,
                v.city.id AS cityId
        FROM Venue v
        ORDER BY v.id
        """)
    Page<VenueSyncProjection> findAllForSync(Pageable pageable);
}
