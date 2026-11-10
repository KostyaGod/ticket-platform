package dev.kostya.ticketplatform.event.services;

import dev.kostya.ticketplatform.event.dto.base.EntityReference;
import dev.kostya.ticketplatform.event.dto.request.EventCatalogFilter;
import dev.kostya.ticketplatform.event.dto.request.EventCreateRequest;
import dev.kostya.ticketplatform.event.dto.request.EventFilter;
import dev.kostya.ticketplatform.event.dto.request.EventUpdateRequest;
import dev.kostya.ticketplatform.event.dto.response.EventCatalogResponse;
import dev.kostya.ticketplatform.event.dto.base.PageModel;
import dev.kostya.ticketplatform.event.dto.response.EventDetailsResponse;
import dev.kostya.ticketplatform.event.entity.Event;
import dev.kostya.ticketplatform.event.enums.DeletedViewFilter;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface EventService {
    PageModel<EventCatalogResponse> getCatalog(
            Long cityId,
            EventCatalogFilter filter,
            Pageable pageable
    );

    PageModel<EntityReference<Event>> getReferences(
            String nameFilter,
            Pageable pageable
    );

    EventDetailsResponse getDetails(Long eventId);

    EventDetailsResponse getDetails(Long eventId, DeletedViewFilter deletedFilter);

    PageModel<EventDetailsResponse> getEvents(EventFilter filter, Pageable pageable);

    EventDetailsResponse create(EventCreateRequest request);

    @Transactional
    EventDetailsResponse update(Long id, EventUpdateRequest request);

    void delete(Long id, boolean cascade);

    EventDetailsResponse restore(Long id, boolean cascade);
}
