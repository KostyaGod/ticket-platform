package dev.kostya.ticketplatform.infrastructure.controllers;

import dev.kostya.ticketplatform.common.dto.base.PageModel;
import dev.kostya.ticketplatform.common.dto.response.ErrorResponse;
import dev.kostya.ticketplatform.common.dto.base.EntityReference;
import dev.kostya.ticketplatform.infrastructure.dto.request.VenueCreateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.request.VenueFilter;
import dev.kostya.ticketplatform.infrastructure.dto.request.VenueUpdateRequest;
import dev.kostya.ticketplatform.infrastructure.dto.response.VenueDetailsResponse;
import dev.kostya.ticketplatform.infrastructure.dto.response.VenueWithChildrenResponse;
import dev.kostya.ticketplatform.infrastructure.entity.Venue;
import dev.kostya.ticketplatform.infrastructure.services.VenueService;
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

@Tag(name = "Площадки (Кинотеатры)", description = "Управление площадками, кинотеатрами и локациями проведения мероприятий")
@RestController
@RequestMapping("/venues")
@RequiredArgsConstructor
public class VenueController {
    private final VenueService venueService;

    @Operation(
            summary = "Краткий список площадок (справочник)",
            description = "Возвращает легковесный список площадок (ID и имя) для выпадающих меню с фильтром по названию"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список площадок успешно получен")
    })
    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public PageModel<EntityReference<Venue>> getVenuesReferences(
            @Parameter(description = "Поиск по названию площадки")
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
        return venueService.getReferences( nameFilter, pageable );
    }

    @Operation(
            summary = "Детальный список площадок с фильтрацией",
            description = "Позволяет получить пагинированный список площадок с фильтрами по городу, адресу и статусу"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список площадок успешно получен"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public PageModel<VenueDetailsResponse> getVenues(
            @ModelAttribute VenueFilter filter,
            @PageableDefault(
                    size = 20,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return venueService.getVenues( filter, pageable );
    }

    @Operation(
            summary = "Информация о площадке с дочерними залами",
            description = "Возвращает детальную карточку площадки с перечнем всех относящихся к ней залов"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Площадка найдена",
                    content = @Content(schema = @Schema(implementation = VenueWithChildrenResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Площадка не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public VenueWithChildrenResponse getVenue(
            @Parameter(description = "ID площадки", required = true)
            @PathVariable Long id
    ) {
        return venueService.getVenue( id );
    }

    @Operation(
            summary = "Создание площадки",
            description = "Добавляет новую площадку (кинотеатр) с привязкой к городу"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Площадка создана",
                    content = @Content(schema = @Schema(implementation = VenueDetailsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public VenueDetailsResponse createVenue(
            @Valid @RequestBody VenueCreateRequest request
    ) {
        return venueService.create( request );
    }

    @Operation(
            summary = "Обновление параметров площадки",
            description = "Редактирует адрес, название или контактные данные существующей площадки"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Площадка успешно обновлена",
                    content = @Content(schema = @Schema(implementation = VenueDetailsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Площадка не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public VenueDetailsResponse updateVenue(
            @Parameter(description = "ID площадки", required = true)
            @PathVariable Long id,
            @Valid @RequestBody VenueUpdateRequest request
    ) {
        return venueService.update( id, request );
    }

    @Operation(
            summary = "Мягкое удаление площадки",
            description = "Помечает площадку как удаленную (soft delete)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Площадка удалена"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Площадка не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVenue(
            @Parameter(description = "ID площадки", required = true)
            @PathVariable Long id
    ) {
        venueService.delete( id, false );
    }

    @Operation(
            summary = "Восстановление площадки",
            description = "Восстанавливает удаленную ранее площадку"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Площадка восстановлена",
                    content = @Content(schema = @Schema(implementation = VenueDetailsResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Площадка не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @PostMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.OK)
    public VenueDetailsResponse restoreVenue(
            @Parameter(description = "ID площадки", required = true)
            @PathVariable Long id
    ) {
        return venueService.restore( id, false );
    }
}
