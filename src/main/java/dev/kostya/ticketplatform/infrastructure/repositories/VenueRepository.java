package dev.kostya.ticketplatform.infrastructure.repositories;

import dev.kostya.ticketplatform.infrastructure.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Long> {
}
