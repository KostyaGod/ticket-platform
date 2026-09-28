package dev.kostya.ticketplatform.event.controllers;

import dev.kostya.ticketplatform.event.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController {
    public final EventService eventService;

    @GetMapping()
    public ResponseEntity<?> getEvents(
            @PathVariable Integer page,
            @PathVariable Integer size,
            @PathVariable Long cityId
    ) {

    }
}
