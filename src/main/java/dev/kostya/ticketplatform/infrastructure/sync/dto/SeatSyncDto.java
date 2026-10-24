package dev.kostya.ticketplatform.infrastructure.sync.dto;

import dev.kostya.ticketplatform.infrastructure.entity.Seat;

public record SeatSyncDto(
        Long id,
        int rowNumber,
        int seatNumber,
        Long hallId
) {
    public SeatSyncDto(Seat seat) {
        this(
                seat.getId(),
                seat.getRowNumber(),
                seat.getSeatNumber(),
                seat.getHallId()
        );
    }
}
