package dev.kostya.ticketplatform.auth.exception;

import dev.kostya.ticketplatform.auth.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidCredentialsException.class)
    public ErrorResponse handleInvalidCredentials(
            InvalidCredentialsException ex
    ) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        return new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                null
        );
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ErrorResponse handleEmailAlreadyExists(
            EmailAlreadyExistsException ex
    ) {
        HttpStatus status = HttpStatus.CONFLICT;

        return new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                null
        );
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ErrorResponse handleUsernameAlreadyExists(
            UsernameAlreadyExistsException ex
    ) {
        HttpStatus status = HttpStatus.CONFLICT;

        return new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                null
        );
    }

    @ResponseStatus(HttpStatus.FORBIDDEN )
    @ExceptionHandler(UserBlockedException.class)
    public ErrorResponse handleUserBlocked(
            UserBlockedException ex
    ) {
        HttpStatus status = HttpStatus.FORBIDDEN;

        return new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                null
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationException(
            MethodArgumentNotValidException ex
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing
                ));

        return new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                "Validation failed",
                errors
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CurrentPasswordMismatchException.class)
    public ErrorResponse handleCurrentPasswordMismatch(
            CurrentPasswordMismatchException ex
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                null
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NewPasswordMatchesOldException.class)
    public ErrorResponse handleNewPasswordMatchesOld(
            NewPasswordMatchesOldException ex
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                null
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserNotFoundException.class)
    public ErrorResponse handleUserNotFound(
            UserNotFoundException ex
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                null
        );
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(SessionNotFoundException.class)
    public ErrorResponse handleSessionNotFound(
            SessionNotFoundException ex
    ) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        return new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                null
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AdminSelfModificationException.class)
    public ErrorResponse handleAdminSelfModification(
            AdminSelfModificationException ex
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                null
        );
    }
}
