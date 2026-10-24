package dev.kostya.ticketplatform.infrastructure.dto.response;

import dev.kostya.ticketplatform.infrastructure.dto.base.EntityMetadata;
import dev.kostya.ticketplatform.infrastructure.dto.base.EntityReference;
import dev.kostya.ticketplatform.infrastructure.entity.City;
import dev.kostya.ticketplatform.infrastructure.entity.Venue;

import java.util.List;

public record CityWithChildrenResponse(
        Long id,
        String name,
        String timezone,
        EntityMetadata<City> metadata,
        List<EntityReference<Venue>> children
) { }
