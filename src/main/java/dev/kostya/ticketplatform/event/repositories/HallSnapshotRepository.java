package dev.kostya.ticketplatform.event.repositories;

import dev.kostya.ticketplatform.event.entity.HallSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HallSnapshotRepository extends JpaRepository<HallSnapshot, Long> {
}
