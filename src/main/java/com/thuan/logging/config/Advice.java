package com.thuan.logging.config;


import com.thuan.logging.exceptions.ErrorResponse;
import com.thuan.logging.exceptions.StudentException;
import com.thuan.logging.exceptions.ValidateAddressException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class Advice {
    @ExceptionHandler(StudentException.class)
    public ResponseEntity<ErrorResponse> handleStudentExceptions(StudentException e) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY; // 404
        return new ResponseEntity<>(new ErrorResponse(status, e.getMessage()), status);
    }

    @ExceptionHandler(ValidateAddressException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(ValidateAddressException e) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY; // 404
        return new ResponseEntity<>(new ErrorResponse(status, e.getMessage()), status);
    }
}
