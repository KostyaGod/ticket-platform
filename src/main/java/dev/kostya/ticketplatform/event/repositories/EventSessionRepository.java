package dev.kostya.ticketplatform.event.repositories;

import dev.kostya.ticketplatform.event.entity.EventSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventSessionRepository extends JpaRepository<EventSession, Long> {
}
