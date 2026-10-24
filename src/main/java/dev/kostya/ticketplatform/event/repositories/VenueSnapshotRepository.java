package dev.kostya.ticketplatform.event.repositories;

import dev.kostya.ticketplatform.event.entity.VenueSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VenueSnapshotRepository extends JpaRepository<VenueSnapshot, Long> {
}
