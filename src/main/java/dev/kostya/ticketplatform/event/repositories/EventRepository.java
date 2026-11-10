package dev.kostya.ticketplatform.event.repositories;

import dev.kostya.ticketplatform.event.dto.base.EntityReference;
import dev.kostya.ticketplatform.event.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface EventRepository
        extends JpaRepository<Event, Long>,
        JpaSpecificationExecutor<Event> {

    Optional<Event> findByIdAndDeletedAtIsNull(Long aLong);
    Optional<Event> findByIdAndDeletedAtIsNotNull(Long aLong);

    Page<EntityReference<Event>> findByNameContainingIgnoreCaseAndDeletedAtIsNull(
            String name,
            Pageable pageable
    );
}
