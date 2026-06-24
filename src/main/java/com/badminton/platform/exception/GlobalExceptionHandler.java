package com.badminton.platform.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //  handle email trùng
    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<?> handleEmailExists(EmailExistsException ex) {
        return ResponseEntity
                .badRequest() // 400
                .body(ex.getMessage()); // "EMAIL_EXISTS"
    }

    //  fallback tất cả lỗi khác
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneral(Exception ex) {
        return ResponseEntity
                .status(500)
                .body("INTERNAL_ERROR");
    }
}