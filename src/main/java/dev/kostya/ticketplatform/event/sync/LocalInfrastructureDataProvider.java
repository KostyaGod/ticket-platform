package dev.kostya.ticketplatform.event.sync;

import dev.kostya.ticketplatform.event.dto.base.PageModel;
import dev.kostya.ticketplatform.event.sync.dto.CitySnapshotDto;
import dev.kostya.ticketplatform.event.sync.dto.HallSnapshotDto;
import dev.kostya.ticketplatform.event.sync.dto.VenueSnapshotDto;
import org.springframework.stereotype.Component;

@Component
public class LocalInfrastructureDataProvider implements InfrastructureDataProvider {
    @Override
    public PageModel<CitySnapshotDto> getCities(int page, int size) {
        return null;
    }

    @Override
    public PageModel<VenueSnapshotDto> getVenues(int page, int size) {
        return null;
    }

    @Override
    public PageModel<HallSnapshotDto> getHalls(int page, int size) {
        return null;
    }


}
