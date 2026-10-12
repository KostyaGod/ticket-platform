package dev.kostya.ticketplatform.event.dto.request;

import dev.kostya.ticketplatform.event.enums.DeletedViewFilter;
import dev.kostya.ticketplatform.event.enums.EntityStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EventFilter(
        String title,
        String description,
        String category,
        Integer ageLimit,
        LocalDate releaseDateAfter,
        LocalDate releaseDateBefore,
        EntityStatus status,
        LocalDateTime createdAfter,
        LocalDateTime createdBefore,
        DeletedViewFilter deletedFilter
) { }
