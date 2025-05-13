package com.alisimsek.librarymanagement.common.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageBundleService messageBundleService;

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex) {
        log.error(ex.getMessage(), ex);
        String localizedMessage = getLocalizedMessage(ex);
        ErrorResponse errorResponse = getErrorResponse(ex.getHttpStatus(), localizedMessage, ex.getErrorCode(), null);
        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ErrorResponse errorResponse = getErrorResponse(HttpStatus.BAD_REQUEST, null, null, errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse errorResponse = getErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), "INVALID_JSON", null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse errorResponse = getErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), "FORBIDDEN", null);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> processAuthentication(AuthenticationException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse errorResponse = getErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), "UNAUTHORIZED", null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse errorResponse = getErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), "UNEXPECTED ERROR", null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    private String getLocalizedMessage(BaseException ex) {
        return messageBundleService.getMessage("exception.message.".concat(ex.getMessageKey()), ex.getArgs());
    }

    private static ErrorResponse getErrorResponse(HttpStatus httpStatus, String message, String errorCode, Map<String, String> fieldErrors) {
        return ErrorResponse.builder()
                .httpStatus(httpStatus.value())
                .message(message)
                .errorCode(errorCode)
                .validationErrors(fieldErrors)
                .build();
    }
}
