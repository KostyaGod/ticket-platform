package dev.kostya.ticketplatform.infrastructure.sync.service;

import dev.kostya.ticketplatform.infrastructure.dto.base.PageModel;
import dev.kostya.ticketplatform.infrastructure.sync.dto.CitySyncDto;
import dev.kostya.ticketplatform.infrastructure.sync.dto.HallSyncDto;
import dev.kostya.ticketplatform.infrastructure.sync.dto.SeatSyncDto;
import dev.kostya.ticketplatform.infrastructure.sync.dto.VenueSyncDto;
import org.springframework.data.domain.Pageable;

public interface SyncService {
    PageModel<CitySyncDto> getCities(Pageable pageable);

    PageModel<VenueSyncDto> getVenues(Pageable pageable);

    PageModel<HallSyncDto> getHalls(Pageable pageable);

    PageModel<SeatSyncDto> getSeats(Pageable pageable);
}
