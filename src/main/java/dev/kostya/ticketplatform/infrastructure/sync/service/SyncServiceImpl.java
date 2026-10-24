package dev.kostya.ticketplatform.infrastructure.sync.service;

import dev.kostya.ticketplatform.infrastructure.dto.base.PageMeta;
import dev.kostya.ticketplatform.infrastructure.dto.base.PageModel;
import dev.kostya.ticketplatform.infrastructure.mapper.CityMapper;
import dev.kostya.ticketplatform.infrastructure.mapper.HallMapper;
import dev.kostya.ticketplatform.infrastructure.mapper.SeatMapper;
import dev.kostya.ticketplatform.infrastructure.mapper.VenueMapper;
import dev.kostya.ticketplatform.infrastructure.repositories.CityRepository;
import dev.kostya.ticketplatform.infrastructure.repositories.HallRepository;
import dev.kostya.ticketplatform.infrastructure.repositories.SeatRepository;
import dev.kostya.ticketplatform.infrastructure.repositories.VenueRepository;
import dev.kostya.ticketplatform.infrastructure.sync.dto.CitySyncDto;
import dev.kostya.ticketplatform.infrastructure.sync.dto.HallSyncDto;
import dev.kostya.ticketplatform.infrastructure.sync.dto.SeatSyncDto;
import dev.kostya.ticketplatform.infrastructure.sync.dto.VenueSyncDto;
import dev.kostya.ticketplatform.infrastructure.sync.projection.CitySyncProjection;
import dev.kostya.ticketplatform.infrastructure.sync.projection.HallSyncProjection;
import dev.kostya.ticketplatform.infrastructure.sync.projection.SeatSyncProjection;
import dev.kostya.ticketplatform.infrastructure.sync.projection.VenueSyncProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SyncServiceImpl implements SyncService {
    private final CityRepository cityRepository;
    private final VenueRepository venueRepository;
    private final HallRepository hallRepository;
    private final SeatRepository seatRepository;

    private final CityMapper cityMapper;
    private final VenueMapper venueMapper;
    private final HallMapper hallMapper;
    private final SeatMapper seatMapper;

    @Override
    public PageModel<CitySyncDto> getCities(Pageable pageable) {
        Page<CitySyncProjection> page = cityRepository.findAllForSync( pageable );

        PageMeta meta = new PageMeta(page);
        List<CitySyncDto> content = page.get()
                .map( cityMapper::toCitySyncDto )
                .toList();

        return new PageModel<>(
                content,
                meta
        );
    }

    @Override
    public PageModel<VenueSyncDto> getVenues(Pageable pageable) {
        Page<VenueSyncProjection> page = venueRepository.findAllForSync( pageable );

        PageMeta meta = new PageMeta(page);
        List<VenueSyncDto> content = page.get()
                .map( venueMapper::toVenueSyncDto )
                .toList();

        return new PageModel<>(
                content,
                meta
        );
    }

    @Override
    public PageModel<HallSyncDto> getHalls(Pageable pageable) {
        Page<HallSyncProjection> page = hallRepository.findAllForSync( pageable );

        PageMeta meta = new PageMeta(page);
        List<HallSyncDto> content = page.get()
                .map( hallMapper::toHallSyncDto )
                .toList();

        return new PageModel<>(
                content,
                meta
        );
    }

    @Override
    public PageModel<SeatSyncDto> getSeats(Pageable pageable) {
        Page<SeatSyncProjection> page = seatRepository.findAllForSync( pageable );

        PageMeta meta = new PageMeta(page);
        List<SeatSyncDto> content = page.get()
                .map( seatMapper::toSeatSyncDto )
                .toList();

        return new PageModel<>(
                content,
                meta
        );
    }
}
