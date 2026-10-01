package com.ansari.seat_reserve.exception;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.ansari.seat_reserve.dto.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<ApiResponse<Map<String, Object>>> body(HttpStatus status, String errorCode, String message, HttpServletRequest r, Map<String, Object> extra) {
        Map<String, Object> details = new java.util.LinkedHashMap<>();
        details.put("error_code", errorCode);
        details.put("path", r.getRequestURI());
        if (extra != null) details.putAll(extra);
        return ResponseEntity.status(status).body(new ApiResponse<>(status.value(), false, message, details, Instant.now()));
    }

    @ExceptionHandler(ApiException.class)
    ResponseEntity<ApiResponse<Map<String, Object>>> api(ApiException e, HttpServletRequest r) {
        return body(e.status(), e.code(), e.getMessage(), r, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Map<String, Object>>> validation(MethodArgumentNotValidException e, HttpServletRequest r) {
        Map<String, String> fields = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, fe -> fe.getDefaultMessage() == null ? "invalid" : fe.getDefaultMessage(), (a, b) -> a));
        return body(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Request validation failed", r, Map.of("fields", fields));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ApiResponse<Map<String, Object>>> constraint(ConstraintViolationException e, HttpServletRequest r) {
        return body(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", e.getMessage(), r, null);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class, IllegalArgumentException.class})
    ResponseEntity<ApiResponse<Map<String, Object>>> badRequest(Exception e, HttpServletRequest r) {
        return body(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "Malformed or invalid request", r, null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<ApiResponse<Map<String, Object>>> notFound(NoResourceFoundException e, HttpServletRequest r) {
        return body(HttpStatus.NOT_FOUND, "NOT_FOUND", "Resource not found", r, null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ApiResponse<Map<String, Object>>> methodNotAllowed(HttpRequestMethodNotSupportedException e, HttpServletRequest r) {
        return body(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", e.getMessage(), r, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ApiResponse<Map<String, Object>>> accessDenied(AccessDeniedException e, HttpServletRequest r) {
        return body(HttpStatus.FORBIDDEN, "FORBIDDEN", "Access denied", r, null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApiResponse<Map<String, Object>>> conflict(DataIntegrityViolationException e, HttpServletRequest r) {
        log.warn("Data integrity violation at {}: {}", r.getRequestURI(), e.getMessage());
        return body(HttpStatus.CONFLICT, "CONFLICT", "Request conflicts with existing data", r, null);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiResponse<Map<String, Object>>> generic(Exception e, HttpServletRequest r) {
        log.error("Unhandled error at {}", r.getRequestURI(), e);
        return body(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Internal server error", r, null);
    }
}
