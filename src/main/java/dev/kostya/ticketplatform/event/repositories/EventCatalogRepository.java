package dev.kostya.ticketplatform.event.repositories;

import dev.kostya.ticketplatform.event.dto.request.EventCatalogFilter;
import dev.kostya.ticketplatform.event.dto.response.EventCatalogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventCatalogRepository {
    Page<EventCatalogResponse> findCatalogWithFilters(
            Long cityId,
            EventCatalogFilter filter,
            Pageable pageable
    );
}
