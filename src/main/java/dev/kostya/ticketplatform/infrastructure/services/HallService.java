package dev.kostya.ticketplatform.infrastructure.services;

import dev.kostya.ticketplatform.infrastructure.dto.base.EntityReference;
import dev.kostya.ticketplatform.infrastructure.dto.request.HallCreateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.request.HallFilter;
import dev.kostya.ticketplatform.infrastructure.dto.request.HallUpdateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.base.PageModel;
import dev.kostya.ticketplatform.infrastructure.dto.response.HallDetailsResponse;
import dev.kostya.ticketplatform.infrastructure.dto.response.HallWithChildrenResponse;
import dev.kostya.ticketplatform.infrastructure.entity.Hall;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface HallService {
    PageModel<EntityReference<Hall>> getReferences(String nameFilter, Pageable pageable);

    HallWithChildrenResponse getHall(Long hallId);

    PageModel<HallDetailsResponse> getHalls(HallFilter filter, Pageable pageable);

    @Transactional
    HallDetailsResponse create(HallCreateRequest request);

    @Transactional
    HallDetailsResponse update(Long id, HallUpdateRequest request);

    @Transactional
    void delete(Long id, boolean cascade);

    @Transactional
    HallDetailsResponse restore(Long id, boolean cascade);
}
