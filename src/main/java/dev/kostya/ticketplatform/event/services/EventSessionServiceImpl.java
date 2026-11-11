package dev.kostya.ticketplatform.event.services;

import dev.kostya.ticketplatform.common.dto.base.PageModel;
import dev.kostya.ticketplatform.common.dto.base.Range;
import dev.kostya.ticketplatform.common.exception.EntityNotDeletedException;
import dev.kostya.ticketplatform.common.exception.EntityNotFoundException;
import dev.kostya.ticketplatform.event.dto.request.SessionCreateRequest;
import dev.kostya.ticketplatform.event.dto.request.SessionFilter;
import dev.kostya.ticketplatform.event.dto.request.SessionUpdateRequest;
import dev.kostya.ticketplatform.event.dto.response.SessionDetailsResponse;
import dev.kostya.ticketplatform.event.dto.response.SessionShortResponse;
import dev.kostya.ticketplatform.event.entity.EventSession;
import dev.kostya.ticketplatform.common.enums.DeletedViewFilter;
import dev.kostya.ticketplatform.event.mapper.SessionMapper;
import dev.kostya.ticketplatform.event.repositories.EventSessionRepository;
import dev.kostya.ticketplatform.event.specifications.EventSessionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventSessionServiceImpl implements EventSessionService {
    private final EventSessionRepository sessionRepository;
    private final SessionMapper sessionMapper;

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
            Long eventId,
            Range<Instant> dateFilter,
            Range<LocalTime> timeFilter,
            ZoneId timezone
    ) {
        List<Instant> dates = sessionRepository
                .findStartTimesByEventIdAndRange(
                        eventId,
                        dateFilter.from(),
                        dateFilter.to()
                );

        return dates.stream()
                .map( instant -> instant.atZone(timezone) )
                .filter( zdt -> isWithinTimeRange( zdt.toLocalTime(), timeFilter ) )
                .map( ZonedDateTime::toLocalDate )
                .distinct()
                .toList();

    }

    @Override
    public SessionDetailsResponse create(SessionCreateRequest request) {
        EventSession session = sessionMapper.toEntity( request );
        sessionRepository.save( session );
        return sessionMapper.toDetailsResponse( session );
    }

    @Override
    public SessionDetailsResponse update(Long id, SessionUpdateRequest request) {
        EventSession session = sessionRepository.findById( id )
                .orElseThrow( () -> new EntityNotFoundException( "EventSession", id ));

        sessionMapper.updateEntityFromDto( request, session );

        return sessionMapper.toDetailsResponse( session );
    }

    @Override
    public void delete(Long id, boolean cascade) {
        EventSession session = sessionRepository.findById( id )
                .orElseThrow( () -> new EntityNotFoundException( "EventSession", id ) );

        UUID operatorId = UUID.randomUUID();
        session.markDeleted( operatorId );

        if (cascade) {
            // Каскадный вызов микросервисов для удаления связанных сущностей
            // В частности Scope Table
        }
    }

    @Override
    public SessionDetailsResponse restore(Long id, boolean cascade) {
        EventSession session = sessionRepository.findById( id )
                .orElseThrow( () -> new EntityNotFoundException( "EventSession", id ) );

        if ( !session.isDeleted() ) {
            throw new EntityNotDeletedException( "EventSession", id );
        }

        UUID operationId = session.restore();

        if (cascade) {
            // Каскадный вызов микросервисов для восстановления связанных сущностей
            // В частности Scope Table
        }

        return sessionMapper.toDetailsResponse( session );
    }

    private boolean isWithinTimeRange(
            LocalTime  time,
            Range<LocalTime> timeFilter
    ) {
        if (timeFilter == null || (timeFilter.from() == null && timeFilter.to() == null)) {
            return true;
        }

        LocalTime from = timeFilter.from() != null ? timeFilter.from() : LocalTime.MIN;
        LocalTime to = timeFilter.to() != null ? timeFilter.to() : LocalTime.MAX;

        if (!from.isAfter(to)) {
            return !time.isBefore(from) && !time.isAfter(to);
        }

        return !(time.isBefore(from) && time.isAfter(to));
    }
}
