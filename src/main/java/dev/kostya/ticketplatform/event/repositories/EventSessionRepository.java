package dev.kostya.ticketplatform.event.repositories;

import dev.kostya.ticketplatform.event.entity.EventSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EventSessionRepository
        extends JpaRepository<EventSession, Long>,
        JpaSpecificationExecutor<EventSession> {
}
