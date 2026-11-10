package dev.kostya.ticketplatform.infrastructure.repositories;

import dev.kostya.ticketplatform.infrastructure.dto.base.EntityReference;
import dev.kostya.ticketplatform.infrastructure.entity.Venue;
import dev.kostya.ticketplatform.infrastructure.sync.dto.VenueSyncDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface VenueRepository
        extends JpaRepository<Venue, Long>
        , JpaSpecificationExecutor<Venue> {

    @Query("""
        SELECT v.id,
                v.name,
                v.city.id
        FROM Venue v
        ORDER BY v.id
        """)
    Page<VenueSyncDto> findAllForSync(Pageable pageable);

    Page<EntityReference<Venue>> findByNameContainingIgnoreCaseAndDeletedAtIsNull(
            String name,
            Pageable pageable
    );
}
