package org.cttelsamicsterrassa.data.api.rest.error;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleAllExceptions(Exception ex) {
        ErrorResponseDto dto = new ErrorResponseDto("INTERNAL_ERROR", ex.getMessage() == null ? "Unexpected error" : ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(dto);
    }

    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        Map<String, Object> details = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> details.put(err.getField(), err.getDefaultMessage()));
        ErrorResponseDto dto = new ErrorResponseDto("VALIDATION_ERROR", "Validation failed", details);
        return ResponseEntity.badRequest().body(dto);
    }
}

