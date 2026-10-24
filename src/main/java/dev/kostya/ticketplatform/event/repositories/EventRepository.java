package dev.kostya.ticketplatform.event.repositories;

import dev.kostya.ticketplatform.event.entity.Event;
import dev.kostya.ticketplatform.event.entity.IdentifiableAndNamed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EventRepository
        extends JpaRepository<Event, Long>,
        JpaSpecificationExecutor<Event> {

    Page<IdentifiableAndNamed> findByNameContainingIgnoreCase(
            String name,
            Pageable pageable
    );
}
