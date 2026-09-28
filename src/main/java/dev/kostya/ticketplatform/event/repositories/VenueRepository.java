package dev.kostya.ticketplatform.event.repositories;

import dev.kostya.ticketplatform.event.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Long> {
}
