package dev.kostya.ticketplatform.infrastructure.services;

import dev.kostya.ticketplatform.infrastructure.dto.base.EntityReference;
import dev.kostya.ticketplatform.infrastructure.dto.request.CityCreateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.request.CityFilter;
import dev.kostya.ticketplatform.infrastructure.dto.request.CityUpdateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.base.PageModel;
import dev.kostya.ticketplatform.infrastructure.dto.response.CityDetailsResponse;
import dev.kostya.ticketplatform.infrastructure.dto.response.CityWithChildrenResponse;
import dev.kostya.ticketplatform.infrastructure.entity.City;
import dev.kostya.ticketplatform.infrastructure.exception.EntityNotDeletedException;
import dev.kostya.ticketplatform.infrastructure.exception.EntityNotFoundException;
import dev.kostya.ticketplatform.infrastructure.mapper.CityMapper;
import dev.kostya.ticketplatform.infrastructure.repositories.CityRepository;
import dev.kostya.ticketplatform.infrastructure.specifications.CitySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;
    private final CityMapper cityMapper;

    @Override
    public PageModel<EntityReference<City>> getReferences(
            String nameFilter,
            Pageable pageable
    ) {
        Page<EntityReference<City>> page = cityRepository
                .findByNameContainingIgnoreCaseAndDeletedAtIsNull( nameFilter, pageable);

        return new PageModel<>( page );
    }

    @Override
    public CityWithChildrenResponse getCity(Long cityId) {
        City city = cityRepository.findById( cityId )
                .orElseThrow( () -> new EntityNotFoundException( "City", cityId ) );

        return cityMapper.toWithChildrenResponse( city );
    }

    @Override
    public PageModel<CityDetailsResponse> getCities(CityFilter filter, Pageable pageable) {
        Specification<City> spec = CitySpecification.filterByCityFilter( filter );
        Page<City> page = cityRepository.findAll( spec, pageable );

        return new PageModel<>(
                page,
                cityMapper::toDetailsResponseList
        );
    }

    @Override
    public CityDetailsResponse create(CityCreateRequest request) {
        City city = cityRepository.save( cityMapper.toEntity( request ) );

        return cityMapper.toDetailsResponse( city );
    }

    @Transactional
    @Override
    public CityDetailsResponse update(Long id, CityUpdateRequest request) {
        City city = cityRepository.findById( id )
                .orElseThrow( () -> new EntityNotFoundException( "City", id ) );

        cityMapper.updateEntityFromDto( request, city );

        return cityMapper.toDetailsResponse( city );
    }

    @Transactional
    @Override
    public void delete(Long id, boolean cascade) {
        City city = cityRepository.findById( id )
                .orElseThrow( () -> new EntityNotFoundException( "City", id ) );

        UUID operationId = city.markDeleted();

        if (cascade) {
            // Каскадный вызов микросервисов для удаления связанных сущностей
            // В частности Venue, Hall, Seat
        }
    }

    @Transactional
    @Override
    public CityDetailsResponse restore(Long id, boolean cascade) {
        City city = cityRepository.findById(id)
                .orElseThrow( () -> new EntityNotFoundException( "City", id ) );

        if ( !city.isDeleted() ) {
            throw new EntityNotDeletedException("City", id);
        }

        UUID operationId = city.restore();

        if (cascade) {
            // Каскадный вызов микросервисов для восстановления связанных сущностей
            // В частности Venue, Hall, Seat
        }

        return cityMapper.toDetailsResponse( city );
    }
}
