package dev.kostya.ticketplatform.infrastructure.services;

import dev.kostya.ticketplatform.infrastructure.dto.base.EntityReference;
import dev.kostya.ticketplatform.infrastructure.dto.request.SeatCreateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.request.SeatFilter;
import dev.kostya.ticketplatform.infrastructure.dto.request.SeatUpdateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.base.PageModel;
import dev.kostya.ticketplatform.infrastructure.dto.response.SeatDetailsResponse;
import dev.kostya.ticketplatform.infrastructure.entity.Seat;
import dev.kostya.ticketplatform.infrastructure.exception.EntityNotDeletedException;
import dev.kostya.ticketplatform.infrastructure.exception.EntityNotFoundException;
import dev.kostya.ticketplatform.infrastructure.mapper.SeatMapper;
import dev.kostya.ticketplatform.infrastructure.repositories.SeatRepository;
import dev.kostya.ticketplatform.infrastructure.specifications.SeatSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {
    private final SeatRepository seatRepository;
    private final SeatMapper seatMapper;

    @Override
    public PageModel<EntityReference<Seat>> getReferences(
            Integer rowNumber,
            Integer seatNumber,
            Pageable pageable
    ) {
        Specification<Seat> spec = SeatSpecification.filterByRowNumberAndSeatNumber(
                rowNumber,
                seatNumber
        );

        Page<Seat> page = seatRepository.findAll( spec, pageable );

        return new PageModel<>(
                page.map(seatMapper::toReference)
        );
    }

    @Override
    public SeatDetailsResponse getSeat(Long seatId) {
        Seat seat = seatRepository.findById( seatId )
                .orElseThrow( () -> new EntityNotFoundException( "Seat", seatId ) );

        return seatMapper.toDetailsResponse( seat );
    }

    @Override
    public PageModel<SeatDetailsResponse> getSeats(
            SeatFilter filter,
            Pageable pageable
    ) {
        Specification<Seat> spec = SeatSpecification.filterBySeatFilter( filter );

        Page<Seat> page = seatRepository.findAll( spec, pageable );

        return new PageModel<>(
                page.map( seatMapper::toDetailsResponse )
        );
    }

    @Override
    public SeatDetailsResponse create(SeatCreateRequest request) {
        Seat seat = seatRepository.save( seatMapper.toEntity( request ) );
        return seatMapper.toDetailsResponse( seat );
    }

    @Transactional
    @Override
    public SeatDetailsResponse update(Long id, SeatUpdateRequest request) {
        Seat seat = seatRepository.findById( id )
                .orElseThrow( () -> new EntityNotFoundException( "Seat", id ) );

        seatMapper.updateEntityFromDto( request, seat );

        return seatMapper.toDetailsResponse( seat );
    }

    @Override
    public void delete(Long id, boolean cascade) {
        Seat seat = seatRepository.findById( id )
                .orElseThrow( () -> new EntityNotFoundException( "Seat", id ) );

        UUID operationId = seat.markDeleted();

        if (cascade) {
            // Каскадный вызов микросервисов для удаления связанных сущностей
        }
    }

    @Transactional
    @Override
    public SeatDetailsResponse restore(Long id, boolean cascade) {
        Seat seat = seatRepository.findById( id )
                .orElseThrow( () -> new EntityNotFoundException( "Seat", id ) );

        if ( !seat.isDeleted() ) {
            throw new EntityNotDeletedException("Seat", id);
        }

        UUID operationId = seat.restore();

        if (cascade) {
            // Каскадный вызов микросервисов для восстановления связанных сущностей
        }

        return seatMapper.toDetailsResponse( seat );
    }
}
