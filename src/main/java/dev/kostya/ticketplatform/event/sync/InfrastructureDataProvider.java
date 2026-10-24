package dev.kostya.ticketplatform.event.sync;

import dev.kostya.ticketplatform.event.dto.base.PageModel;
import dev.kostya.ticketplatform.event.sync.dto.CitySnapshotDto;
import dev.kostya.ticketplatform.event.sync.dto.HallSnapshotDto;
import dev.kostya.ticketplatform.event.sync.dto.VenueSnapshotDto;

public interface InfrastructureDataProvider {
    PageModel<CitySnapshotDto> getCities(int page, int size);

    PageModel<VenueSnapshotDto> getVenues(int page, int size);

    PageModel<HallSnapshotDto> getHalls(int page, int size);
}
