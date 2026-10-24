package dev.kostya.ticketplatform.infrastructure.dto.response;

import dev.kostya.ticketplatform.infrastructure.dto.base.EntityMetadata;
import dev.kostya.ticketplatform.infrastructure.entity.City;

public record CityDetailsResponse(
        Long id,
        String name,
        String timezone,
        EntityMetadata<City> metadata
) { }
