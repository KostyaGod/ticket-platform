package dev.kostya.ticketplatform.infrastructure.dto.response;

import dev.kostya.ticketplatform.infrastructure.dto.base.EntityMetadata;
import dev.kostya.ticketplatform.infrastructure.dto.base.EntityReference;
import dev.kostya.ticketplatform.infrastructure.entity.Hall;
import dev.kostya.ticketplatform.infrastructure.entity.Venue;

public record HallDetailsResponse(
        Long id,
        String name,
        EntityReference<Venue> venue,
        EntityMetadata<Hall> metadata
) { }
