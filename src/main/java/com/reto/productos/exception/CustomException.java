package com.reto.productos.exception;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {
    
    private final HttpStatus status;
    private final String severity;

    public CustomException(String message, HttpStatus status, String severity) {
        super(message);
        this.status = status;
        this.severity = severity;
    }
    
}
