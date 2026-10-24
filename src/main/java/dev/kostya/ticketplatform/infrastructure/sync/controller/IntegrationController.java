package dev.kostya.ticketplatform.infrastructure.sync.controller;

import dev.kostya.ticketplatform.infrastructure.dto.base.PageModel;
import dev.kostya.ticketplatform.infrastructure.sync.dto.SeatSyncDto;
import dev.kostya.ticketplatform.infrastructure.sync.service.SyncService;
import dev.kostya.ticketplatform.infrastructure.sync.dto.CitySyncDto;
import dev.kostya.ticketplatform.infrastructure.sync.dto.HallSyncDto;
import dev.kostya.ticketplatform.infrastructure.sync.dto.VenueSyncDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/sync")
@RequiredArgsConstructor
public class IntegrationController {
    private final SyncService syncService;

    @GetMapping("/cities")
    public PageModel<CitySyncDto> getCities(Pageable pageable) {
        return syncService.getCities(pageable);
    }

    @GetMapping("/venues")
    public PageModel<VenueSyncDto> getVenues(Pageable pageable) {
        return syncService.getVenues(pageable);
    }

    @GetMapping("/halls")
    public PageModel<HallSyncDto> getHalls(Pageable pageable) {
        return syncService.getHalls(pageable);
    }

    @GetMapping("/seats")
    public PageModel<SeatSyncDto> getSeats(Pageable pageable) {
        return syncService.getSeats(pageable);
    }
}
