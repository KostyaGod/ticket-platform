package dev.kostya.ticketplatform.infrastructure.repositories;

import dev.kostya.ticketplatform.infrastructure.entity.Hall;
import dev.kostya.ticketplatform.infrastructure.sync.projection.HallSyncProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HallRepository extends JpaRepository<Hall, Long> {
    @Query("""
        SELECT h.id AS id,
                h.name AS name,
                h.venue.id AS venueId
        FROM Hall h
        ORDER BY h.id
        """)
    Page<HallSyncProjection> findAllForSync(Pageable pageable);
}
