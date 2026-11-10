package dev.kostya.ticketplatform.infrastructure.services;

import dev.kostya.ticketplatform.infrastructure.dto.base.EntityReference;
import dev.kostya.ticketplatform.infrastructure.dto.request.HallCreateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.request.HallFilter;
import dev.kostya.ticketplatform.infrastructure.dto.request.HallUpdateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.base.PageModel;
import dev.kostya.ticketplatform.infrastructure.dto.response.HallDetailsResponse;
import dev.kostya.ticketplatform.infrastructure.dto.response.HallWithChildrenResponse;
import dev.kostya.ticketplatform.infrastructure.entity.Hall;
import dev.kostya.ticketplatform.infrastructure.exception.EntityNotDeletedException;
import dev.kostya.ticketplatform.infrastructure.exception.EntityNotFoundException;
import dev.kostya.ticketplatform.infrastructure.mapper.HallMapper;
import dev.kostya.ticketplatform.infrastructure.repositories.HallRepository;
import dev.kostya.ticketplatform.infrastructure.specifications.HallSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HallServiceImpl implements HallService {
    private final HallRepository hallRepository;
    private final HallMapper hallMapper;

    @Override
    public PageModel<EntityReference<Hall>> getReferences(
            String nameFilter,
            Pageable pageable
    ) {
        Page<EntityReference<Hall>> page = hallRepository
                .findByNameContainingIgnoreCaseAndDeletedAtIsNull( nameFilter, pageable );

        return new PageModel<>( page );
    }

    @Override
    public HallWithChildrenResponse getHall(Long hallId) {
        Hall hall = hallRepository.findById( hallId )
                .orElseThrow( () -> new EntityNotFoundException( "Hall", hallId ) );

        return hallMapper.toWithChildrenResponse( hall );
    }

    @Override
    public PageModel<HallDetailsResponse> getHalls(
            HallFilter filter,
            Pageable pageable
    ) {
        Specification<Hall> spec = HallSpecification.filterByHallFilter( filter );
        Page<Hall> page = hallRepository.findAll( spec, pageable );

        return new PageModel<>(
                page,
                hallMapper::toDetailsResponseList
        );
    }

    @Transactional
    @Override
    public HallDetailsResponse create(HallCreateRequest request) {
        Hall hall = hallRepository.save( hallMapper.toEntity( request ) );
        return hallMapper.toDetailsResponse( hall );
    }

    @Transactional
    @Override
    public HallDetailsResponse update(Long id, HallUpdateRequest request) {
        Hall hall = hallRepository.findById( id )
                .orElseThrow( () -> new EntityNotFoundException( "Hall", id ) );

        hallMapper.updateEntityFromDto( request, hall );

        return hallMapper.toDetailsResponse( hall );
    }

    @Transactional
    @Override
    public void delete(Long id, boolean cascade) {
        Hall hall = hallRepository.findById( id )
                .orElseThrow( () -> new EntityNotFoundException( "Hall", id ) );

        UUID operationId = hall.markDeleted();

        if (cascade) {
            // Каскадный вызов микросервисов для удаления связанных сущностей
            // В частности Seat
        }
    }

    @Transactional
    @Override
    public HallDetailsResponse restore(Long id, boolean cascade) {
        Hall hall = hallRepository.findById(id)
                .orElseThrow( () -> new EntityNotFoundException( "Hall", id ) );

        if ( !hall.isDeleted() ) {
            throw new EntityNotDeletedException("Hall", id);
        }

        UUID operationId = hall.restore();

        if (cascade) {
            // Каскадный вызов микросервисов для восстановления связанных сущностей
            // В частности Seat
        }

        return hallMapper.toDetailsResponse( hall );
    }
}
