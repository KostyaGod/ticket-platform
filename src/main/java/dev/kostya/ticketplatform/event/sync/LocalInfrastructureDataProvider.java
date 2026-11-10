package dev.kostya.ticketplatform.event.sync;

import dev.kostya.ticketplatform.event.dto.base.PageMeta;
import dev.kostya.ticketplatform.event.dto.base.PageModel;
import dev.kostya.ticketplatform.event.sync.dto.CitySnapshotDto;
import dev.kostya.ticketplatform.event.sync.dto.HallSnapshotDto;
import dev.kostya.ticketplatform.event.sync.dto.VenueSnapshotDto;
import dev.kostya.ticketplatform.infrastructure.sync.controller.IntegrationController;
import dev.kostya.ticketplatform.infrastructure.sync.dto.CitySyncDto;
import dev.kostya.ticketplatform.infrastructure.sync.dto.HallSyncDto;
import dev.kostya.ticketplatform.infrastructure.sync.dto.VenueSyncDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LocalInfrastructureDataProvider implements InfrastructureDataProvider {
    private final IntegrationController integrationController;

    @Override
    public PageModel<CitySnapshotDto> getCities(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        dev.kostya.ticketplatform.infrastructure.dto.base.PageModel<CitySyncDto> response =
                integrationController.getCities( pageable );

        List<CitySnapshotDto> context = response.model().stream()
                .map(dto -> new CitySnapshotDto(
                        dto.id(),
                        dto.name()
                ))
                .toList();

        return new PageModel<>(
                context,
                new PageMeta(
                        response.pageMeta().page(),
                        response.pageMeta().size(),
                        response.pageMeta().totalElements(),
                        response.pageMeta().totalPages(),
                        response.pageMeta().hasNext()
                )
        );
    }

    @Override
    public PageModel<VenueSnapshotDto> getVenues(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        dev.kostya.ticketplatform.infrastructure.dto.base.PageModel<VenueSyncDto> response =
                integrationController.getVenues( pageable );

        List<VenueSnapshotDto> context = response.model().stream()
                .map(dto -> new VenueSnapshotDto(
                        dto.id(),
                        dto.name(),
                        dto.cityId()
                ))
                .toList();

        return new PageModel<>(
                context,
                new PageMeta(
                        response.pageMeta().page(),
                        response.pageMeta().size(),
                        response.pageMeta().totalElements(),
                        response.pageMeta().totalPages(),
                        response.pageMeta().hasNext()
                )
        );
    }

    @Override
    public PageModel<HallSnapshotDto> getHalls(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        dev.kostya.ticketplatform.infrastructure.dto.base.PageModel<HallSyncDto> response =
                integrationController.getHalls( pageable );

        List<HallSnapshotDto> context = response.model().stream()
                .map(dto -> new HallSnapshotDto(
                        dto.id(),
                        dto.name(),
                        dto.venueId()
                ))
                .toList();

        return new PageModel<>(
                context,
                new PageMeta(
                        response.pageMeta().page(),
                        response.pageMeta().size(),
                        response.pageMeta().totalElements(),
                        response.pageMeta().totalPages(),
                        response.pageMeta().hasNext()
                )
        );
    }


}
