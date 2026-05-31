package com.reto.productos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.reto.productos.util.Constants;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .errorId(UUID.randomUUID())
                .mensaje(ex.getMessage())
                .severidad(ex.getSeverity())
                .codigoEstado(ex.getStatus().value())
                .detalles(Collections.singletonList("Excepción controlada en la capa de negocio."))
                .build();
        return new ResponseEntity<>(error, ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse error = ErrorResponse.builder()
                .errorId(UUID.randomUUID())
                .mensaje("Ha ocurrido un error inesperado en el servidor interno.")
                .severidad(Constants.SEVERITY_FATAL)
                .codigoEstado(500)
                .detalles(Collections.singletonList(ex.getMessage()))
                .build();
        return ResponseEntity.status(500).body(error);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            org.springframework.dao.DataIntegrityViolationException ex) {
        String mensaje = "Ha ocurrido un conflicto con la integridad de los datos.";

        // Analizamos si el log menciona tu restricción única de Oracle
        if (ex.getMessage() != null && ex.getMessage().contains("UK_PRODUCTO_CODIGO")) {
            mensaje = "El código del producto ya se encuentra registrado en el sistema."; // Tu mensaje de negocio
        }

        ErrorResponse error = ErrorResponse.builder()
                .errorId(UUID.randomUUID())
                .mensaje(mensaje)
                .severidad(Constants.SEVERITY_ERROR)
                .codigoEstado(HttpStatus.CONFLICT.value()) // Un 409 Conflict es el estándar REST correcto aquí
                .detalles(Collections.singletonList("Violación de restricción única en la base de datos Oracle."))
                .build();

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            org.springframework.web.bind.MethodArgumentNotValidException ex) {
        // Captura el primer mensaje de error que encuentre en la lista de campos
        // inválidos
        List<String> listaDeErrores = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(org.springframework.validation.FieldError::getDefaultMessage)
                .collect(java.util.stream.Collectors.toList());

        // 2. Definimos un mensaje genérico para el encabezado principal
        String mensajePrincipal = "Se encontraron errores de validación en los datos enviados.";

        // 3. Construimos el ErrorResponse inyectando la lista completa en 'detalles'
        ErrorResponse error = ErrorResponse.builder()
                .errorId(UUID.randomUUID())
                .mensaje(mensajePrincipal)
                .severidad(Constants.SEVERITY_ERROR)
                .codigoEstado(HttpStatus.BAD_REQUEST.value())
                .detalles(listaDeErrores) // <-- AQUÍ ENTRA LA LISTA COMPLETA
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}
