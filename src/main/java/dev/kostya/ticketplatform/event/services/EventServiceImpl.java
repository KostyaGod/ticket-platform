package dev.kostya.ticketplatform.event.services;

import dev.kostya.ticketplatform.event.dto.base.EntityReference;
import dev.kostya.ticketplatform.event.dto.request.EventCatalogFilter;
import dev.kostya.ticketplatform.event.dto.request.EventCreateRequest;
import dev.kostya.ticketplatform.event.dto.request.EventFilter;
import dev.kostya.ticketplatform.event.dto.request.EventUpdateRequest;
import dev.kostya.ticketplatform.event.dto.response.EventCatalogResponse;
import dev.kostya.ticketplatform.event.dto.base.PageMeta;
import dev.kostya.ticketplatform.event.dto.base.PageModel;
import dev.kostya.ticketplatform.event.dto.response.EventDetailsResponse;
import dev.kostya.ticketplatform.event.entity.Event;
import dev.kostya.ticketplatform.event.entity.IdentifiableAndNamed;
import dev.kostya.ticketplatform.event.exception.EntityNotFoundException;
import dev.kostya.ticketplatform.event.mapper.EventMapper;
import dev.kostya.ticketplatform.event.repositories.EventCatalogRepository;
import dev.kostya.ticketplatform.event.repositories.EventRepository;
import dev.kostya.ticketplatform.event.specifications.EventSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventCatalogRepository catalogRepository;
    private final EventMapper eventMapper;

    @Override
    public PageModel<EventCatalogResponse> getCatalog(
            Long cityId,
            EventCatalogFilter filter,
            Pageable pageable
    ) {
        Page<EventCatalogResponse> page =
                catalogRepository.findCatalogWithFilters(
                        cityId,
                        filter,
                        pageable
                );
        PageMeta meta = new PageMeta(page);

        return new PageModel<>(
                page.getContent(),
                meta
        );
    }

    @Override
    public PageModel<EntityReference<Event>> getReferences(
            String nameFilter,
            Pageable pageable
    ) {
        Page<IdentifiableAndNamed> page = eventRepository
                .findByNameContainingIgnoreCase( nameFilter, pageable );

        List<EntityReference<Event>> content = page.getContent().stream()
                .map( eventMapper::toReference )
                .toList();

        return new PageModel<>(
                content,
                new PageMeta(page)
        );
    }

    @Override
    public EventDetailsResponse getDetails(Long eventId) {
        Event event = eventRepository.findById( eventId )
                .orElseThrow( () -> new EntityNotFoundException( "Event", eventId ) );

        return eventMapper.toDetailsResponse( event );
    }

    @Override
    public PageModel<EventDetailsResponse> getEvents(
            EventFilter filter,
            Pageable pageable
    ) {
        Specification<Event> spec = EventSpecification.forFilter(filter);
        Page<Event> page = eventRepository.findAll(spec, pageable);

        List<EventDetailsResponse> content = page.getContent().stream()
                .map( eventMapper::toDetailsResponse )
                .toList();

        return new PageModel<>(
                content,
                new PageMeta(page)
        );
    }

    @Override
    public EventDetailsResponse create(EventCreateRequest request) {
        Event event = eventRepository.save( eventMapper.toEntity( request ) );
        return eventMapper.toDetailsResponse( event );
    }

    @Transactional
    @Override
    public EventDetailsResponse update(Long id, EventUpdateRequest request) {
        Event event = eventRepository.findById( id )
                .orElseThrow( () -> new EntityNotFoundException( "Event", id ) );

        eventMapper.updateEntityFromDto( request, event );

        return eventMapper.toDetailsResponse( event );
    }

    @Override
    public void delete(Long id, boolean cascade) {
        Event event = eventRepository.findById( id )
                .orElseThrow( () -> new EntityNotFoundException( "Event", id ) );

        UUID operatorId = UUID.randomUUID();
        event.markDeleted( operatorId );

        if (cascade) {
            // Каскадный вызов микросервисов для удаления связанных сущностей
            // В частности Scope Table
        }
    }

    @Transactional
    @Override
    public EventDetailsResponse restore(Long id, boolean cascade) {
        Event event = eventRepository.findById(id)
                .orElseThrow( () -> new EntityNotFoundException( "Event", id ) );

        UUID operationId = event.restore();

        if (cascade) {
            // Каскадный вызов микросервисов для восстановления связанных сущностей
            // В частности Scope Table
        }

        return eventMapper.toDetailsResponse( event );
    }
}
