package dev.kostya.ticketplatform.event.services;

import dev.kostya.ticketplatform.event.dto.request.EventCatalogFilter;
import dev.kostya.ticketplatform.event.dto.response.EventCatalogResponse;
import dev.kostya.ticketplatform.event.dto.base.PageModel;
import org.springframework.data.domain.Pageable;

public interface EventService {
    PageModel<EventCatalogResponse> getEventsCatalog(
            Long cityId,
            EventCatalogFilter filter,
            Pageable pageable
    );
}
