package dev.kostya.ticketplatform.event.repositories;

import dev.kostya.ticketplatform.event.entity.CitySnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CitySnapshotRepository extends JpaRepository<CitySnapshot, Long> {
}
