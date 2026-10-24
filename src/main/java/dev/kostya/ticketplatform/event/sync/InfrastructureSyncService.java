package dev.kostya.ticketplatform.event.sync;

import dev.kostya.ticketplatform.event.dto.base.PageModel;
import dev.kostya.ticketplatform.event.services.InfrastructureSnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class InfrastructureSyncService {
    private static final int BATCH_SIZE = 500;

    private final InfrastructureSnapshotService infrastructureService;
    private final InfrastructureDataProvider dataProvider;

    @Transactional
    public void synchronize() {
        synchronizeCities();
        synchronizeVenues();
        synchronizeHalls();
    }

    public void synchronizeCities() {
        processPages(
                dataProvider::getCities,
                infrastructureService::saveAllCitiesByDto
        );
    }

    public void synchronizeVenues() {
        processPages(
                dataProvider::getVenues,
                infrastructureService::saveAllVenuesByDto
        );
    }

    public void synchronizeHalls() {
        processPages(
                dataProvider::getHalls,
                infrastructureService::saveAllHallsByDto
        );
    }

    private <T> void processPages(
            BiFunction<Integer, Integer, PageModel<T>> fetch,
            Consumer<List<T>> save
    ) {
        int page = 0;

        while (true) {
            PageModel<T> result = fetch.apply(page, BATCH_SIZE);

            save.accept(result.model());

            if (!result.pageMeta().hasNext()) {
                break;
            }

            page++;
        }
    }
}
