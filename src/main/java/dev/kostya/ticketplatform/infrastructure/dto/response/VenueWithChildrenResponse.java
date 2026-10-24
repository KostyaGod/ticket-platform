package dev.kostya.ticketplatform.infrastructure.dto.response;

import dev.kostya.ticketplatform.infrastructure.dto.base.EntityMetadata;
import dev.kostya.ticketplatform.infrastructure.dto.base.EntityReference;
import dev.kostya.ticketplatform.infrastructure.entity.City;
import dev.kostya.ticketplatform.infrastructure.entity.Hall;
import dev.kostya.ticketplatform.infrastructure.entity.Venue;

import java.util.List;

public record VenueWithChildrenResponse(
        Long id,
        String name,
        EntityReference<City> city,
        EntityMetadata<Venue> metadata,
        List<EntityReference<Hall>> children
) { }
