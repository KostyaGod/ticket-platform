package dev.kostya.ticketplatform.event.repositories;

import dev.kostya.ticketplatform.event.dto.base.EntityMetadata;
import dev.kostya.ticketplatform.event.dto.base.EntityReference;
import dev.kostya.ticketplatform.event.dto.response.SessionDetailsResponse;
import dev.kostya.ticketplatform.event.dto.response.SessionShortResponse;
import dev.kostya.ticketplatform.event.entity.*;
import dev.kostya.ticketplatform.event.enums.EntityStatus;
import dev.kostya.ticketplatform.event.enums.EventSessionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.Instant;
import java.util.UUID;

public interface EventSessionRepository
        extends JpaRepository<EventSession, Long>,
        JpaSpecificationExecutor<EventSession> {

    interface NamedInfo {
        Long getId();
        String getName();

        default <T extends IdentifiableAndNamed> EntityReference<T> getReference() {
            return new EntityReference<T>(
                    getId(),
                    getName()
            );
        }
    }

    interface ShortProjection {
        Long getId();
        NamedInfo getVenue();
        Instant getStartAt();
    }

    default Page<SessionShortResponse> findAllShort(
            Specification<EventSession> spec,
            Pageable pageable
    ) {
        return findBy(
                spec,
                q -> q.as( ShortProjection.class )
                        .page( pageable )
        ).map( p -> new SessionShortResponse(
                p.getId(),
                p.getVenue().getReference(),
                p.getStartAt()
        ));
    }

    interface DetailsProjection {
        Long getId();
        NamedInfo getEvent();
        NamedInfo getHall();
        NamedInfo getVenue();
        NamedInfo getCity();
        Long getOrganizationId();
        EventSessionStatus getEventStatus();
        Instant getSalesStartAt();
        Instant getStartAt();
        Instant getEndAt();

        Instant getCreatedAt();
        Instant getDeletedAt();
        UUID getDeletionOperationId();
        EntityStatus getStatus();
    }

    default Page<SessionDetailsResponse> findAllDetails(
            Specification<EventSession> spec,
            Pageable pageable
    ) {
        return findBy(
                spec,
                q -> q.as( DetailsProjection.class )
                        .page( pageable )
        ).map( p -> new SessionDetailsResponse(
                p.getId(),
                p.getEvent().getReference(),
                p.getHall().getReference(),
                p.getVenue().getReference(),
                p.getCity().getReference(),
                p.getOrganizationId(),
                p.getEventStatus(),
                p.getSalesStartAt(),
                p.getStartAt(),
                p.getEndAt(),
                new EntityMetadata<>(
                        p.getCreatedAt(),
                        p.getDeletedAt(),
                        p.getDeletionOperationId(),
                        p.getStatus()
                )
        ));
    }
}
