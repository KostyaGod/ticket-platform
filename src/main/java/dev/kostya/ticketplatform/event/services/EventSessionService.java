package dev.kostya.ticketplatform.event.services;

import dev.kostya.ticketplatform.common.dto.base.PageModel;
import dev.kostya.ticketplatform.common.dto.base.Range;
import dev.kostya.ticketplatform.event.dto.request.SessionCreateRequest;
import dev.kostya.ticketplatform.event.dto.request.SessionFilter;
import dev.kostya.ticketplatform.event.dto.request.SessionUpdateRequest;
import dev.kostya.ticketplatform.event.dto.response.SessionDetailsResponse;
import dev.kostya.ticketplatform.event.dto.response.SessionShortResponse;
import dev.kostya.ticketplatform.common.enums.DeletedViewFilter;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

public interface EventSessionService {
    PageModel<SessionShortResponse> getShortSessions(
            Range<Instant> dateFilter,
            Long eventId,
            Pageable pageable
    );

    PageModel<SessionShortResponse> getSessionsByEvent(
            Long eventId,
            DeletedViewFilter deletedFilter,
            Pageable pageable
    );

    PageModel<SessionDetailsResponse> getDetailedSessions(
            SessionFilter filter,
            Pageable pageable
    );

    List<LocalDate> getSessionDates(
            Long eventId,
            Range<Instant> dateFilter,
            Range<LocalTime> timeFilter,
            ZoneId timezone
    );

    SessionDetailsResponse create(
            SessionCreateRequest request
    );

    SessionDetailsResponse update(
            Long id,
            SessionUpdateRequest request
    );

    void delete(Long id, boolean cascade);

    SessionDetailsResponse restore(Long id, boolean cascade);
}
