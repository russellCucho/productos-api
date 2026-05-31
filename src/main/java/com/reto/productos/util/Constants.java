package com.reto.productos.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    // Estados del ciclo de vida del producto
    public final String ESTADO_ACTIVO = "A";
    public final String ESTADO_INACTIVO = "I";

    // Mensajes de Validación de Negocio
    public final String MSG_PRECIO_NEGATIVO = "El precio del producto no puede ser inferior a 0.00.";
    public final String MSG_STOCK_NEGATIVO = "El stock disponible no puede ser un número negativo.";
    public final String MSG_CODIGO_REPETIDO = "El código del producto ya se encuentra registrado en el sistema.";
    public final String MSG_PRODUCTO_NOT_FOUND = "El producto solicitado con el ID especificado no existe.";

    // Severidades para el ErrorResponse
    public final String SEVERITY_ERROR = "ERROR_NEGOCIO";
    public final String SEVERITY_FATAL = "FATAL_SISTEMA";
}
