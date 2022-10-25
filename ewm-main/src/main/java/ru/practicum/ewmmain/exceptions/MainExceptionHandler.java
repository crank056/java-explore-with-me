package ru.practicum.ewmmain.exceptions;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class MainExceptionHandler {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(BAD_REQUEST)
    public ApiError handleValidationException(ValidationException e) {
        return new ApiError(e.getStackTrace(), e.getMessage(), "Запрос составлен с ошибкой",
                (BAD_REQUEST.name()), LocalDateTime.now().format(formatter));
    }

    @ExceptionHandler(AccessException.class)
    @ResponseStatus(FORBIDDEN)
    public ApiError handleAccessException(AccessException e) {
        return new ApiError(e.getStackTrace(), e.getMessage(),
                "Не выполнены условия для совершения операции",
                (FORBIDDEN.name()), LocalDateTime.now().format(formatter));
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ApiError handleNotFoundException(NotFoundException e) {
        return new ApiError(e.getStackTrace(), String.format("%s with id %s not found", e.getObjClass(), e.getId()),
                "Объект не найден", NOT_FOUND.name(), LocalDateTime.now().format(formatter));
    }

    @ExceptionHandler({org.hibernate.exception.ConstraintViolationException.class,
            javax.validation.ConstraintViolationException.class})
    @ResponseStatus(CONFLICT)
    public ApiError handleConstraintViolation(RuntimeException e) {
        return new ApiError(e.getStackTrace(), e.getMessage(),
                "Запрос приводит к нарушению целостности данных",
                (CONFLICT.name()), LocalDateTime.now().format(formatter));
    }

    @ExceptionHandler({LimitException.class, StateException.class})
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ApiError handleLimitException(RuntimeException e) {
        return new ApiError(e.getStackTrace(), e.getMessage(), "Внутренняя ошибка сервера",
                (INTERNAL_SERVER_ERROR.name()), LocalDateTime.now().format(formatter));
    }
}
