package dev.kostya.ticketplatform.infrastructure.repositories;

import dev.kostya.ticketplatform.infrastructure.entity.Seat;
import dev.kostya.ticketplatform.infrastructure.sync.projection.SeatSyncProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SeatRepository extends JpaRepository<Seat,Long> {
    @Query("""
        SELECT s.id AS id,
                s.rowNumber AS rowNumber,
                s.seatNumber AS columnNumber,
                s.hall.id AS hallId
        FROM Seat s
        ORDER BY s.id
        """)
    Page<SeatSyncProjection> findAllForSync(Pageable pageable);
}
