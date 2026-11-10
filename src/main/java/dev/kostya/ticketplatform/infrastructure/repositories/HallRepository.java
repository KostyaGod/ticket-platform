package dev.kostya.ticketplatform.infrastructure.repositories;

import dev.kostya.ticketplatform.infrastructure.dto.base.EntityReference;
import dev.kostya.ticketplatform.infrastructure.entity.Hall;
import dev.kostya.ticketplatform.infrastructure.sync.dto.HallSyncDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface HallRepository
        extends JpaRepository<Hall, Long>,
        JpaSpecificationExecutor<Hall> {

    @Query("""
        SELECT h.id, h.name, h.venue.id
        FROM Hall h
        ORDER BY h.id
        """)
    Page<HallSyncDto> findAllForSync(Pageable pageable);

    Page<EntityReference<Hall>> findByNameContainingIgnoreCaseAndDeletedAtIsNull(
            String name,
            Pageable pageable
    );
}
