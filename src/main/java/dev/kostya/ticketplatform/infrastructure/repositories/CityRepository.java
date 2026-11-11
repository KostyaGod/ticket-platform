package dev.kostya.ticketplatform.infrastructure.repositories;

import dev.kostya.ticketplatform.common.dto.base.EntityReference;
import dev.kostya.ticketplatform.infrastructure.entity.City;
import dev.kostya.ticketplatform.infrastructure.sync.dto.CitySyncDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface CityRepository
        extends JpaRepository<City,Long>,
        JpaSpecificationExecutor<City> {

    @Query("""
        SELECT c.id, c.name
        FROM City c
        ORDER BY c.id
        """)
    Page<CitySyncDto> findAllForSync(Pageable pageable);

    Page<EntityReference<City>> findByNameContainingIgnoreCaseAndDeletedAtIsNull(
            String name,
            Pageable pageable
    );
}
