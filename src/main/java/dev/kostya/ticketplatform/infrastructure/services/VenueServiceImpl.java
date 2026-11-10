package dev.kostya.ticketplatform.infrastructure.services;

import dev.kostya.ticketplatform.infrastructure.dto.base.EntityReference;
import dev.kostya.ticketplatform.infrastructure.dto.request.VenueCreateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.request.VenueFilter;
import dev.kostya.ticketplatform.infrastructure.dto.request.VenueUpdateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.base.PageModel;
import dev.kostya.ticketplatform.infrastructure.dto.response.VenueDetailsResponse;
import dev.kostya.ticketplatform.infrastructure.dto.response.VenueWithChildrenResponse;
import dev.kostya.ticketplatform.infrastructure.entity.Venue;
import dev.kostya.ticketplatform.infrastructure.exception.EntityNotDeletedException;
import dev.kostya.ticketplatform.infrastructure.exception.EntityNotFoundException;
import dev.kostya.ticketplatform.infrastructure.mapper.VenueMapper;
import dev.kostya.ticketplatform.infrastructure.repositories.VenueRepository;
import dev.kostya.ticketplatform.infrastructure.specifications.VenueSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VenueServiceImpl implements VenueService {
    private final VenueRepository venueRepository;
    private final VenueMapper venueMapper;

    @Override
    public PageModel<EntityReference<Venue>> getReferences(
            String nameFilter,
            Pageable pageable
    ) {
        Page<EntityReference<Venue>> page = venueRepository
                .findByNameContainingIgnoreCaseAndDeletedAtIsNull( nameFilter, pageable );

        return new PageModel<>( page );
    }

    @Override
    public VenueWithChildrenResponse getVenue(Long venueId) {
        Venue venue = venueRepository.findById( venueId )
                .orElseThrow( () -> new EntityNotFoundException( "Venue", venueId ) );

        return venueMapper.toWithChildrenResponse( venue );
    }

    @Override
    public PageModel<VenueDetailsResponse> getVenues(
            VenueFilter filter,
            Pageable pageable
    ) {
        Specification<Venue> spec = VenueSpecification.filterByVenueFilter( filter );
        Page<Venue> page = venueRepository.findAll( spec, pageable );

        return new PageModel<>(
                page,
                venueMapper::toDetailsResponseList
        );
    }

    @Override
    public VenueDetailsResponse create(VenueCreateRequest request) {
        Venue venue = venueRepository.save( venueMapper.toEntity( request ) );
        return venueMapper.toDetailsResponse( venue );
    }

    @Transactional
    @Override
    public VenueDetailsResponse update(Long id, VenueUpdateRequest request) {
        Venue venue = venueRepository.findById( id )
                .orElseThrow( () -> new EntityNotFoundException( "Venue", id ) );

        venueMapper.updateEntityFromDto( request, venue );

        return venueMapper.toDetailsResponse( venue );
    }

    @Transactional
    @Override
    public void delete(Long id, boolean cascade) {
        Venue venue = venueRepository.findById( id )
                .orElseThrow( () -> new EntityNotFoundException( "Venue", id ) );

        UUID operationId = venue.markDeleted();

        if (cascade) {
            // Каскадный вызов микросервисов для удаления связанных сущностей
            // В частности Hall, Seat
        }
    }

    @Transactional
    @Override
    public VenueDetailsResponse restore(Long id, boolean cascade) {
        Venue venue = venueRepository.findById(id)
                .orElseThrow( () -> new EntityNotFoundException( "Venue", id ) );

        if ( !venue.isDeleted() ) {
            throw new EntityNotDeletedException("Venue", id);
        }

        UUID operationId = venue.restore();

        if (cascade) {
            // Каскадный вызов микросервисов для восстановления связанных сущностей
            // В частности Hall, Seat
        }

        return venueMapper.toDetailsResponse( venue );
    }
}
