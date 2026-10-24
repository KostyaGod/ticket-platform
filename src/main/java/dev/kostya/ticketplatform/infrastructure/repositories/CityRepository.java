package dev.kostya.ticketplatform.infrastructure.repositories;

import dev.kostya.ticketplatform.infrastructure.entity.City;
import dev.kostya.ticketplatform.infrastructure.sync.projection.CitySyncProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CityRepository extends JpaRepository<City,Long> {
    Page<City> findByNameContaining(String name, Pageable pageable);

    @Query("""
        SELECT c.id AS id,
                c.name AS name
        FROM City c
        ORDER BY c.id
        """)
    Page<CitySyncProjection> findAllForSync(Pageable pageable);
}
