package dev.kostya.ticketplatform.infrastructure.sync.projection;

public interface VenueSyncProjection {
    Long getId();
    String getName();
    Long getCityId();
}
