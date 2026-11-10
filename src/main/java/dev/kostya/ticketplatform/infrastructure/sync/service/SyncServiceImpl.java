package dev.kostya.ticketplatform.infrastructure.sync.service;

import dev.kostya.ticketplatform.infrastructure.dto.base.PageModel;
import dev.kostya.ticketplatform.infrastructure.repositories.CityRepository;
import dev.kostya.ticketplatform.infrastructure.repositories.HallRepository;
import dev.kostya.ticketplatform.infrastructure.repositories.SeatRepository;
import dev.kostya.ticketplatform.infrastructure.repositories.VenueRepository;
import dev.kostya.ticketplatform.infrastructure.sync.dto.CitySyncDto;
import dev.kostya.ticketplatform.infrastructure.sync.dto.HallSyncDto;
import dev.kostya.ticketplatform.infrastructure.sync.dto.SeatSyncDto;
import dev.kostya.ticketplatform.infrastructure.sync.dto.VenueSyncDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SyncServiceImpl implements SyncService {
    private final CityRepository cityRepository;
    private final VenueRepository venueRepository;
    private final HallRepository hallRepository;
    private final SeatRepository seatRepository;

    @Override
    public PageModel<CitySyncDto> getCities(Pageable pageable) {
        Page<CitySyncDto> page = cityRepository.findAllForSync( pageable );

        return new PageModel<>( page );
    }

    @Override
    public PageModel<VenueSyncDto> getVenues(Pageable pageable) {
        Page<VenueSyncDto> page = venueRepository.findAllForSync( pageable );

        return new PageModel<>( page );
    }

    @Override
    public PageModel<HallSyncDto> getHalls(Pageable pageable) {
        Page<HallSyncDto> page = hallRepository.findAllForSync( pageable );

        return new PageModel<>( page );
    }

    @Override
    public PageModel<SeatSyncDto> getSeats(Pageable pageable) {
        Page<SeatSyncDto> page = seatRepository.findAllForSync( pageable );

        return new PageModel<>( page );
    }
}
