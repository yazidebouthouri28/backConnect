package tn.esprit.productservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tn.esprit.productservice.dto.ApiResponse;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateResourceException(DuplicateResourceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    // ── FIX: Database unique constraint violations ────────────────────────────
    // Before: fell through to handleGenericException → ugly 500 with raw SQL.
    // After:  clean 409 Conflict with a message identifying the duplicate field.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex) {

        String rootMsg = ex.getRootCause() != null
                ? ex.getRootCause().getMessage() : ex.getMessage();

        String userMessage = "A record with this value already exists.";

        if (rootMsg != null) {
            if (rootMsg.contains("sku")) {
                userMessage = "A product with this SKU already exists. Use a unique SKU or leave it blank.";
            } else if (rootMsg.contains("barcode")) {
                userMessage = "A product with this barcode already exists.";
            } else if (rootMsg.contains("categories") && rootMsg.contains("name")) {
                userMessage = "A category with this name already exists.";
            } else if (rootMsg.contains("slug")) {
                userMessage = "A category with this slug already exists.";
            } else if (rootMsg.contains("Duplicate entry")) {
                // MySQL format: Duplicate entry 'VALUE' for key 'KEY_NAME'
                String[] parts = rootMsg.split("'");
                if (parts.length >= 2) {
                    userMessage = "Duplicate value '" + parts[1] + "' — this field must be unique.";
                }
            }
        }

        log.warn("Data integrity violation: {}", rootMsg);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(userMessage));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Map<String, String>>builder()
                        .success(false)
                        .message("Validation errors")
                        .data(errors)
                        .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Internal server error: " + ex.getMessage()));
    }
}