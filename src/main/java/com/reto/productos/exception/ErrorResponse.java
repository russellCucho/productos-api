package com.reto.productos.exception;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ErrorResponse {
    
    private UUID errorId;
    private String mensaje;
    private String severidad;
    private int codigoEstado;
    private List<String> detalles;

}
