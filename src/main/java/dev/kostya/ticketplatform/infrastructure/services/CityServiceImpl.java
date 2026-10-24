package dev.kostya.ticketplatform.infrastructure.services;

import dev.kostya.ticketplatform.infrastructure.dto.base.EntityReference;
import dev.kostya.ticketplatform.infrastructure.dto.request.CityCreateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.base.PageMeta;
import dev.kostya.ticketplatform.infrastructure.dto.base.PageModel;
import dev.kostya.ticketplatform.infrastructure.entity.City;
import dev.kostya.ticketplatform.infrastructure.enums.EntityStatus;
import dev.kostya.ticketplatform.infrastructure.exception.CityNotFoundException;
import dev.kostya.ticketplatform.infrastructure.repositories.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;

    @Override
    public PageModel<EntityReference<City>> getCityRefList(String name, Pageable pageable) {
        Page<City> page = cityRepository.findByNameContaining(name, pageable);

        List<EntityReference<City>> cities = page.getContent().stream()
                .map(EntityReference::new)
                .toList();

        PageMeta pageMeta = new PageMeta(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext()
        );

        return new PageModel<>(
                cities,
                pageMeta
        );
    }

    @Override
    public EntityReference<City> createCity(CityCreateRequest request) {
        City city = new City();
        city.setName(request.name());
        City created = cityRepository.save(city);

        return new EntityReference<>(created);
    }

    @Transactional
    @Override
    public void deleteCity(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new CityNotFoundException(id));

        city.setStatus(EntityStatus.DELETED);
    }
}
