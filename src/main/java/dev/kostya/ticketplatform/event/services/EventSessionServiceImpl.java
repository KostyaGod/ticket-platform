package dev.kostya.ticketplatform.event.services;

import dev.kostya.ticketplatform.event.dto.base.PageModel;
import dev.kostya.ticketplatform.event.dto.base.Range;
import dev.kostya.ticketplatform.event.dto.request.SessionCreateRequest;
import dev.kostya.ticketplatform.event.dto.request.SessionFilter;
import dev.kostya.ticketplatform.event.dto.request.SessionUpdateRequest;
import dev.kostya.ticketplatform.event.dto.response.SessionDetailsResponse;
import dev.kostya.ticketplatform.event.dto.response.SessionShortResponse;
import dev.kostya.ticketplatform.event.entity.EventSession;
import dev.kostya.ticketplatform.event.enums.DeletedViewFilter;
import dev.kostya.ticketplatform.event.repositories.EventSessionRepository;
import dev.kostya.ticketplatform.event.specifications.EventSessionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventSessionServiceImpl implements EventSessionService {
    private final EventSessionRepository sessionRepository;

    @Override
    public PageModel<SessionShortResponse> getShortSessions(
            Range<Instant> dateFilter,
            Long eventId,
            Pageable pageable
    ) {
        Specification<EventSession> spec = EventSessionSpecification
                .filterByEventAndDateRange( eventId, dateFilter );

        Page<SessionShortResponse> page = sessionRepository
                .findAllShort( spec, pageable );

        return new PageModel<>( page );
    }

    @Override
    public PageModel<SessionShortResponse> getSessionsByEvent(
            Long eventId,
            DeletedViewFilter deletedFilter,
            Pageable pageable
    ) {
        Specification<EventSession> spec = EventSessionSpecification
                .filterByEventAndDeleted( eventId, deletedFilter );

        Page<SessionShortResponse> page = sessionRepository
                .findAllShort( spec, pageable );

        return new PageModel<>( page );
    }

    @Override
    public PageModel<SessionDetailsResponse> getDetailedSessions(
            SessionFilter filter,
            Pageable pageable
    ) {
        Specification<EventSession> spec = EventSessionSpecification
                .filterBySessionFilter( filter );

        Page<SessionDetailsResponse> page = sessionRepository
                .findAllDetails( spec, pageable );

        return new PageModel<>( page );
    }

    @Override
    public List<LocalDate> getSessionDates(
            Range<LocalDate> dateFilter,
            Range<LocalTime> timeFilter,
            Long eventId
    ) {
        return List.of();
    }

    @Override
    public SessionDetailsResponse create(SessionCreateRequest request) {
        return null;
    }

    @Override
    public SessionUpdateRequest update(Long id, SessionUpdateRequest request) {
        return null;
    }

    @Override
    public void delete(Long id, boolean cascade) {

    }

    @Override
    public SessionDetailsResponse restore(Long id, boolean cascade) {
        return null;
    }
}
