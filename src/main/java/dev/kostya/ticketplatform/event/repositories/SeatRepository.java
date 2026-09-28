package dev.kostya.ticketplatform.event.repositories;

import dev.kostya.ticketplatform.event.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat,Long> {
}
