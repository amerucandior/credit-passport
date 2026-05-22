package com.passport.creditpassport.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.UriUtils;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidProfilePhotoUrlException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidPhotoUrl(InvalidProfilePhotoUrlException ex,
                                                                   HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(ProfileAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleProfileAlreadyExists(ProfileAlreadyExistsException ex,
                                                                       HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(DuplicateAuthExceptions.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicate(DuplicateAuthExceptions ex,
                                                            HttpServletRequest request) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(ex.getMessage())
                .path(safeRequestPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex,
                                                          HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(BadCredentialsException ex,
                                                                 HttpServletRequest request) {
        return buildError(HttpStatus.UNAUTHORIZED, "Invalid email/national ID or password", request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(IllegalArgumentException ex,
                                                            HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(IllegalStateException ex,
                                                          HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(OtpDeliveryException.class)
    public ResponseEntity<ApiErrorResponse> handleOtpDelivery(OtpDeliveryException ex,
                                                              HttpServletRequest request) {
        // no stack trace here — EmailService already logged it with full context
        log.warn("OTP delivery failed [retryable={}] at [{}]",
                ex.isRetryable(), request.getRequestURI());

        if (ex.isRetryable()) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Retry-After", "30");
            ApiErrorResponse body = buildError(HttpStatus.SERVICE_UNAVAILABLE,
                    "OTP delivery failed. Please try again later.", request).getBody();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .headers(headers)
                    .body(body);
        }


        return buildError(HttpStatus.UNPROCESSABLE_CONTENT,
                "OTP could not be delivered to this destination.", request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        List<String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        ValidationErrorResponse error = ValidationErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation failed")
                .fieldErrors(fieldErrors)
                .path(request instanceof ServletWebRequest servletWebRequest
                        ? safeRequestPath(servletWebRequest.getRequest())
                        : "/")
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleAll(Exception ex,
                                                      HttpServletRequest request) {
        log.error("Unhandled exception while processing {}", request.getRequestURI(), ex);

        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", request);
    }

    private ResponseEntity<ApiErrorResponse> buildError(HttpStatus status,
                                                       String message,
                                                       HttpServletRequest request) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(safeRequestPath(request))
                .build();

        return ResponseEntity.status(status).body(error);
    }

    private String safeRequestPath(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        if (requestUri == null || requestUri.isBlank()) {
            return "/";
        }
        return UriUtils.encodePath(requestUri, StandardCharsets.UTF_8);
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ApiErrorResponse {
        private Instant timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ValidationErrorResponse {
        private Instant timestamp;
        private int status;
        private String error;
        private String message;
        private List<String> fieldErrors;
        private String path;
    }
}
