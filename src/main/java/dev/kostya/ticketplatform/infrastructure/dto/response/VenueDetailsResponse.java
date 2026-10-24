package dev.kostya.ticketplatform.infrastructure.dto.response;

import dev.kostya.ticketplatform.infrastructure.dto.base.EntityMetadata;
import dev.kostya.ticketplatform.infrastructure.dto.base.EntityReference;
import dev.kostya.ticketplatform.infrastructure.entity.City;
import dev.kostya.ticketplatform.infrastructure.entity.Venue;

public record VenueDetailsResponse(
        Long id,
        String name,
        EntityReference<City> city,
        EntityMetadata<Venue> metadata
) { }
