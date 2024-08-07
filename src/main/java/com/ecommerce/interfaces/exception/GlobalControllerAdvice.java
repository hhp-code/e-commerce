package com.ecommerce.interfaces.exception;

import com.ecommerce.interfaces.exception.domain.CouponException;
import com.ecommerce.interfaces.exception.domain.OrderException;
import com.ecommerce.interfaces.exception.domain.ProductException;
import com.ecommerce.interfaces.exception.domain.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, WebRequest request) {
        return createErrorResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        return createErrorResponse(ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({OrderException.class, UserException.class, ProductException.class, CouponException.class})
    public ResponseEntity<ErrorResponse> handleDomainExceptions(Exception ex, WebRequest request) {
        return createErrorResponse(ex, request, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(Exception ex, WebRequest request, HttpStatus status) {
        log.error("Exception occurred: ", ex);

        String errorMessage = (ex instanceof RuntimeException) ? ex.getMessage() : "An unexpected error occurred";

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                errorMessage,
                request.getDescription(false),
                ex.getClass().getSimpleName()
        );

        return new ResponseEntity<>(errorResponse, status);
    }

    public record ErrorResponse(
            LocalDateTime timestamp,
            int status,
            String error,
            String message,
            String path,
            String exceptionType
    ) {}
}
