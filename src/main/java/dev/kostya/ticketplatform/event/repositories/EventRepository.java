package dev.kostya.ticketplatform.event.repositories;

import dev.kostya.ticketplatform.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
