package dev.kostya.ticketplatform.event.controllers;

import dev.kostya.ticketplatform.event.dto.request.EventCatalogFilter;
import dev.kostya.ticketplatform.event.dto.response.CatalogResponse;
import dev.kostya.ticketplatform.event.dto.response.PageModel;
import dev.kostya.ticketplatform.event.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    public final EventService eventService;

    @GetMapping()
    public PageModel<CatalogResponse> getEventCatalog(
            @ModelAttribute EventCatalogFilter filter,
            @PageableDefault(
                    size = 20,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return null;
    }
}
