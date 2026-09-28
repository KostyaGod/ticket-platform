package dev.kostya.ticketplatform.event.repositories;

import dev.kostya.ticketplatform.event.entity.Hall;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HallRepository extends JpaRepository<Hall, Long> {
}
