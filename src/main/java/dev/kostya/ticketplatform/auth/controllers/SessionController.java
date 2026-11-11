package dev.kostya.ticketplatform.auth.controllers;

import dev.kostya.ticketplatform.auth.dto.response.SessionResponse;
import dev.kostya.ticketplatform.auth.security.UserPrincipal;
import dev.kostya.ticketplatform.auth.services.SessionService;
import dev.kostya.ticketplatform.common.dto.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Сессии", description = "Управление активными сессиями пользователей в Redis")
@Slf4j
@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;

    @Operation(
            summary = "Получение списка активных сессий текущего пользователя",
            description = "Возвращает список всех сессий пользователя с информацией о времени создания, истечения и признаком текущей сессии"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список сессий успешно получен",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SessionResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public List<SessionResponse> getAllMySessions(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return sessionService.getSessions(
                principal.getId(),
                principal.getSessionId()
        );
    }

    @Operation(
            summary = "Завершение конкретной сессии текущего пользователя",
            description = "Удаляет выбранную сессию из Redis по ее UUID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Сессия успешно завершена"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Сессия не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/me/{sessionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMySession(
            @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "Идентификатор сессии (UUID)", required = true)
            @PathVariable UUID sessionId
    ) {
        sessionService.deleteSession(
                sessionId,
                principal.getId()
        );
    }

    @Operation(
            summary = "Завершение всех сессий текущего пользователя",
            description = "Удаляет все сессии пользователя в Redis, завершая все активные входы"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Все сессии успешно завершены"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllMySessions(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        sessionService.deleteAllSessions(principal.getId());
    }

    @Operation(
            summary = "Административное удаление сессии пользователя",
            description = "Позволяет администратору или серверному сервису отозвать конкретную сессию любого пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Сессия успешно удалена"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав (требуется ADMIN или SERVER)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Сессия не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @DeleteMapping("/{userId}/{sessionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSession(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable Long userId,
            @Parameter(description = "Идентификатор сессии (UUID)", required = true)
            @PathVariable UUID sessionId
    ) {
        sessionService.deleteSession(
                sessionId,
                userId
        );
    }

    @Operation(
            summary = "Административное удаление всех сессий пользователя",
            description = "Позволяет администратору отозвать все активные сессии указанного пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Все сессии пользователя успешно завершены"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав (требуется ADMIN или SERVER)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVER')")
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllSession(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable Long userId
    ) {
        sessionService.deleteAllSessions(userId);
    }
}
