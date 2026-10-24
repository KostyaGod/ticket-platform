package dev.kostya.ticketplatform.event.services;

import dev.kostya.ticketplatform.auth.services.SessionService;
import dev.kostya.ticketplatform.event.entity.CitySnapshot;
import dev.kostya.ticketplatform.event.entity.HallSnapshot;
import dev.kostya.ticketplatform.event.entity.VenueSnapshot;
import dev.kostya.ticketplatform.event.mapper.SyncMapper;
import dev.kostya.ticketplatform.event.repositories.CitySnapshotRepository;
import dev.kostya.ticketplatform.event.repositories.HallSnapshotRepository;
import dev.kostya.ticketplatform.event.repositories.VenueSnapshotRepository;
import dev.kostya.ticketplatform.event.sync.dto.CitySnapshotDto;
import dev.kostya.ticketplatform.event.sync.dto.HallSnapshotDto;
import dev.kostya.ticketplatform.event.sync.dto.VenueSnapshotDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InfrastructureSnapshotServiceImpl implements InfrastructureSnapshotService {
    private final CitySnapshotRepository cityRepository;
    private final VenueSnapshotRepository venueRepository;
    private final HallSnapshotRepository hallRepository;
    private final SessionService sessionService;
    private final SyncMapper syncMapper;

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public void saveAllCitiesByDto(List<CitySnapshotDto> cities) {
        List<CitySnapshot> citySnapshots = cities.stream()
                .map(syncMapper::fromDto)
                .toList();

        cityRepository.saveAll(citySnapshots);
    }

    @Override
    public void saveAllVenuesByDto(List<VenueSnapshotDto> venues) {
        List<VenueSnapshot> venueSnapshots = venues.stream()
                .map(dto -> syncMapper.fromDto(dto, entityManager))
                .toList();

        venueRepository.saveAll(venueSnapshots);
    }

    @Override
    public void saveAllHallsByDto(List<HallSnapshotDto> halls) {
        List<HallSnapshot> hallSnapshots = halls.stream()
                .map(dto -> syncMapper.fromDto(dto, entityManager))
                .toList();

        hallRepository.saveAll(hallSnapshots);
    }

    @Override
    public void saveAllCities(List<CitySnapshot> cities) {
        cityRepository.saveAll(cities);
    }

    @Override
    public void saveAllVenues(List<VenueSnapshot> venues) {
        venueRepository.saveAll(venues);
    }

    @Override
    public void saveAllHalls(List<HallSnapshot> halls) {
        hallRepository.saveAll(halls);
    }

    @Override
    public void createCitySnapshot(Long cityId, String cityName) {
        CitySnapshot snapshot = new CitySnapshot(cityId, cityName);
    }

    @Override
    public void createVenueSnapshot(Long venueId, String venueName, Long cityId) {
        CitySnapshot city = cityRepository.findById(cityId)
                .orElseThrow();
        VenueSnapshot snapshot = new VenueSnapshot(venueId, venueName, city);
    }

    @Override
    public void createHallSnapshot(Long hallId, String hallName, Long venueId) {
        VenueSnapshot venue = venueRepository.findById(venueId)
                .orElseThrow();
        HallSnapshot snapshot = new HallSnapshot(hallId, hallName, venue);
    }

    @Override
    public void updateCitySnapshot(Long cityId, String cityName) {
        CitySnapshot city = cityRepository.findById(cityId)
                .orElseThrow();
        city.setName(cityName);

        cityRepository.save(city);
    }

    @Override
    public void updateVenueSnapshot(Long venueId, String venueName, Long cityId) {
        VenueSnapshot venue = venueRepository.findById(venueId)
                .orElseThrow();
        venue.setName(venueName);
        venue.setCity(cityRepository.findById(cityId)
                .orElseThrow());

        venueRepository.save(venue);
    }

    @Override
    public void updateHallSnapshot(Long hallId, String hallName, Long venueId) {
        HallSnapshot hall = hallRepository.findById(hallId)
                .orElseThrow();
        hall.setName(hallName);
        hall.setVenue(venueRepository.findById(venueId)
                .orElseThrow());

        hallRepository.save(hall);
    }

    @Override
    public void deleteCitySnapshots(Long cityId, UUID deleteOperationId) {
        if (cityRepository.existsById(cityId)) {
            // каскадный вызов удаления eventSessions
            // sessionService.deleteAllByCityId

            cityRepository.deleteById(cityId);
        }
    }

    @Override
    public void deleteVenueCitySnapshots(Long venueId, UUID deleteOperationId) {
        if (venueRepository.existsById(venueId)) {
            // каскадный вызов удаления eventSessions
            // sessionService.deleteAllByVenueId

            venueRepository.deleteById(venueId);
        }
    }

    @Override
    public void deleteHallSnapshots(Long hallId, UUID deleteOperationId) {
        if (hallRepository.existsById(hallId)) {
            // каскадный вызов удаления eventSessions
            // sessionService.deleteAllByHallId

            hallRepository.deleteById(hallId);
        }

    }
}
