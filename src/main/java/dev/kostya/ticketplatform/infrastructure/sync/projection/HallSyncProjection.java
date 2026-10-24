package dev.kostya.ticketplatform.infrastructure.sync.projection;

public interface HallSyncProjection {
    Long getId();
    String getName();
    Long getVenueId();
}
