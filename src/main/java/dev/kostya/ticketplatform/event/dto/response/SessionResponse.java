package dev.kostya.ticketplatform.event.dto.response;

import dev.kostya.ticketplatform.event.dto.model.EventDto;
import dev.kostya.ticketplatform.event.dto.model.HallDto;
import dev.kostya.ticketplatform.event.dto.model.VenueDto;
import dev.kostya.ticketplatform.event.enums.EventSessionStatus;

import java.time.Instant;
import java.time.LocalDateTime;

public record SessionResponse(
        Long id,
        EventDto event,
        HallDto hall,
        VenueDto venue,
        Long cityId,
        Long organizationId,
        EventSessionStatus status,
        Instant salesStartAt,
        Instant startAt,
        Instant endAt,
        LocalDateTime createdAt
) { }
