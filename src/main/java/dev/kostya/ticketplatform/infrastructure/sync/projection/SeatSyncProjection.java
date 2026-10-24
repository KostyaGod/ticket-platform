package dev.kostya.ticketplatform.infrastructure.sync.projection;

public interface SeatSyncProjection {
    Long getId();
    int getRowNumber();
    int getSeatNumber();
    Long getHallId();
}
