package dev.kostya.ticketplatform.infrastructure.repositories;

import dev.kostya.ticketplatform.infrastructure.entity.Seat;
import dev.kostya.ticketplatform.infrastructure.sync.dto.SeatSyncDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface SeatRepository
        extends JpaRepository<Seat,Long>,
        JpaSpecificationExecutor<Seat> {
    @Query("""
        SELECT s.id,
                s.rowNumber,
                s.seatNumber,
                s.hall.id
        FROM Seat s
        ORDER BY s.id
        """)
    Page<SeatSyncDto> findAllForSync(Pageable pageable);
}
