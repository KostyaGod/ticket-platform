package dev.kostya.ticketplatform.infrastructure.controllers;

import dev.kostya.ticketplatform.common.dto.response.ErrorResponse;
import dev.kostya.ticketplatform.common.dto.base.EntityReference;
import dev.kostya.ticketplatform.infrastructure.dto.request.HallCreateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.request.HallFilter;
import dev.kostya.ticketplatform.infrastructure.dto.request.HallUpdateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.response.HallDetailsResponse;
import dev.kostya.ticketplatform.common.dto.base.PageModel;
import dev.kostya.ticketplatform.infrastructure.entity.Hall;
import dev.kostya.ticketplatform.infrastructure.dto.response.HallWithChildrenResponse;
import dev.kostya.ticketplatform.infrastructure.services.HallService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Залы", description = "Управление зрительными залами кинотеатров и площадок")
@RestController
@RequestMapping("/halls")
@RequiredArgsConstructor
public class HallController {
    private final HallService hallService;

    @Operation(
            summary = "Краткий список залов (справочник)",
            description = "Возвращает легковесный список залов (ID и название) с фильтрацией по имени"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список залов успешно получен")
    })
    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public PageModel<EntityReference<Hall>> getHallReferences(
            @Parameter(description = "Фильтр по названию зала")
            @RequestParam(
                    name = "filter",
                    required = false,
                    defaultValue = ""
            ) String nameFilter,
            @PageableDefault(
                    size = 20,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return hallService.getReferences( nameFilter, pageable );
    }

    @Operation(
            summary = "Детальный список залов с фильтрацией",
            description = "Возвращает пагинированный список залов с фильтрами по площадке, типу зала и статусу"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список залов получен"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public PageModel<HallDetailsResponse> getHalls(
            @ModelAttribute HallFilter filter,
            @PageableDefault(
                    size = 20,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return hallService.getHalls( filter, pageable );
    }

    @Operation(
            summary = "Информация о зале с местами",
            description = "Возвращает информацию о зале и список посадочных мест"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Зал найден",
                    content = @Content(schema = @Schema(implementation = HallWithChildrenResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Зал не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public HallWithChildrenResponse getHall(
            @Parameter(description = "ID зала", required = true)
            @PathVariable Long id
    ) {
        return hallService.getHall( id );
    }

    @Operation(
            summary = "Создание зала",
            description = "Создает новый зрительный зал с привязкой к конкретной площадке"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Зал успешно создан",
                    content = @Content(schema = @Schema(implementation = HallDetailsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public HallDetailsResponse createHall(
            @Valid @RequestBody HallCreateRequest request
    ) {
        return hallService.create( request );
    }

    @Operation(
            summary = "Обновление параметров зала",
            description = "Изменяет название, вместимость или конфигурацию зала"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Зал обновлен",
                    content = @Content(schema = @Schema(implementation = HallDetailsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Зал не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public HallDetailsResponse updateHall(
            @Parameter(description = "ID зала", required = true)
            @PathVariable Long id,
            @Valid @RequestBody HallUpdateRequest request
    ) {
        return hallService.update( id, request );
    }

    @Operation(
            summary = "Мягкое удаление зала",
            description = "Помечает зал как удаленный (soft delete)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Зал удален"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Зал не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHall(
            @Parameter(description = "ID зала", required = true)
            @PathVariable Long id
    ) {
        hallService.delete( id, false );
    }

    @Operation(
            summary = "Восстановление зала",
            description = "Восстанавливает удаленный зал из корзины"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Зал восстановлен",
                    content = @Content(schema = @Schema(implementation = HallDetailsResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Зал не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.OK)
    public HallDetailsResponse restoreHall(
            @Parameter(description = "ID зала", required = true)
            @PathVariable Long id
    ) {
        return hallService.restore( id, false );
    }
}
