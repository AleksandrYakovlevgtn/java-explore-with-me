package ru.practicum.stats.server.exeption;

import io.micrometer.core.lang.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({PersistenceException.class})
    protected ResponseEntity<Object> handleAttemptException(PersistenceException o, WebRequest request) {
        String message = "Неудачная попытка сохранения.";
        return makeResponseEntity(message, o, BAD_REQUEST, request);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException o,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatus status,
                                                                  @NonNull WebRequest request) {
        String message = "Нечитаемый JSON.";
        return makeResponseEntity(message, o, status, request);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException o,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatus status,
                                                                  @NonNull WebRequest request) {
        String message = "Неверная дата.";
        return makeResponseEntity(message, o, status, request);
    }

    @ExceptionHandler
    protected ResponseEntity<Object> handleThrowableException(Throwable o, WebRequest request) {
        String message = "Неизвестная ошибка";
        return makeResponseEntity(message, o, INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<Object> makeResponseEntity(String message,
                                                      Throwable o,
                                                      HttpStatus status,
                                                      WebRequest request) {
        ErrorResponse errorResponse = makeBody(
                message,
                status,
                request,
                o);
        return new ResponseEntity<>(errorResponse, status);
    }

    private ErrorResponse makeBody(String message, HttpStatus status, WebRequest request, Throwable o) {
        List<String> reasons;
        if (o instanceof BindException) {
            reasons = ((BindException) o)
                    .getAllErrors()
                    .stream()
                    .map(this::getErrorString)
                    .collect(Collectors.toList());
        } else {
            reasons = Arrays.stream(o.getMessage().split(", ")).collect(Collectors.toList());
        }
        return new ErrorResponse(
                reasons,
                status.value(),
                message,
                getRequestURI(request));
    }

    private String getErrorString(ObjectError error) {
        if (error instanceof FieldError) {
            return ((FieldError) error).getField() + ' ' + error.getDefaultMessage();
        }
        return error.getDefaultMessage();
    }

    private String getRequestURI(WebRequest request) {
        if (request instanceof ServletWebRequest) {
            HttpServletRequest requestHttp = ((ServletWebRequest) request).getRequest();
            return String.format("%s %s", requestHttp.getMethod(), requestHttp.getRequestURI());
        } else {
            return "";
        }
    }
}
