package com.badminton.platform.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // handle email trùng
    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<?> handleEmailExists(EmailExistsException ex) {
        return ResponseEntity
                .badRequest() // 400
                .body(ex.getMessage()); // "EMAIL_EXISTS"
    }

    // fallback tất cả lỗi khác
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneral(Exception ex) {
        return ResponseEntity
                .status(500)
                .body("INTERNAL_ERROR");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException ex) {

        String msg = ex.getMessage();

        if ("INVALID_OLD_PASSWORD".equals(msg)) {
            return ResponseEntity.badRequest().body("INVALID_OLD_PASSWORD");
        }

        if ("PASSWORD_TOO_SHORT".equals(msg)) {
            return ResponseEntity.badRequest().body("PASSWORD_TOO_SHORT");
        }

        return ResponseEntity.status(500).body("INTERNAL_ERROR");
    }
}