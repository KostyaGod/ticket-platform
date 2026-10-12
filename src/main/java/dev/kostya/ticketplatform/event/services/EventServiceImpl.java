package dev.kostya.ticketplatform.event.services;

import dev.kostya.ticketplatform.event.dto.request.EventCatalogFilter;
import dev.kostya.ticketplatform.event.dto.response.EventCatalogResponse;
import dev.kostya.ticketplatform.event.dto.response.PageMeta;
import dev.kostya.ticketplatform.event.dto.response.PageModel;
import dev.kostya.ticketplatform.event.repositories.EventCatalogRepository;
import dev.kostya.ticketplatform.event.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventCatalogRepository catalogRepository;

    public PageModel<EventCatalogResponse> getEventsCatalog(
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
        PageMeta meta = new PageMeta(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext()
        );

        return new PageModel<>(
                page.getContent(),
                meta
        );
    }
}
