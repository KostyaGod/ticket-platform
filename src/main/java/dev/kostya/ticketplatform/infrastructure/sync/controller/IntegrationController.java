package dev.kostya.ticketplatform.infrastructure.sync.controller;

import dev.kostya.ticketplatform.common.dto.base.PageModel;
import dev.kostya.ticketplatform.infrastructure.sync.dto.SeatSyncDto;
import dev.kostya.ticketplatform.infrastructure.sync.service.SyncService;
import dev.kostya.ticketplatform.infrastructure.sync.dto.CitySyncDto;
import dev.kostya.ticketplatform.infrastructure.sync.dto.HallSyncDto;
import dev.kostya.ticketplatform.infrastructure.sync.dto.VenueSyncDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Внутренняя синхронизация (Sync API)", description = "Эндпоинты для выгрузки данных инфраструктуры смежными микросервисами платформы")
@RestController
@RequestMapping("/internal/sync")
@RequiredArgsConstructor
public class IntegrationController {
    private final SyncService syncService;

    @Operation(
            summary = "Синхронизация городов",
            description = "Выгружает пагинированный список городов в формате интеграционного DTO для внешних сервисов"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Данные синхронизации городов получены")
    })
    @GetMapping("/cities")
    public PageModel<CitySyncDto> getCities(Pageable pageable) {
        return syncService.getCities(pageable);
    }

    @Operation(
            summary = "Синхронизация площадок",
            description = "Выгружает пагинированный список площадок/кинотеатров для внешних сервисов"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Данные синхронизации площадок получены")
    })
    @GetMapping("/venues")
    public PageModel<VenueSyncDto> getVenues(Pageable pageable) {
        return syncService.getVenues(pageable);
    }

    @Operation(
            summary = "Синхронизация залов",
            description = "Выгружает пагинированный список зрительных залов для внешних сервисов"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Данные синхронизации залов получены")
    })
    @GetMapping("/halls")
    public PageModel<HallSyncDto> getHalls(Pageable pageable) {
        return syncService.getHalls(pageable);
    }

    @Operation(
            summary = "Синхронизация посадочных мест",
            description = "Выгружает пагинированный список посадочных мест для внешних сервисов"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Данные синхронизации мест получены")
    })
    @GetMapping("/seats")
    public PageModel<SeatSyncDto> getSeats(Pageable pageable) {
        return syncService.getSeats(pageable);
    }
}
