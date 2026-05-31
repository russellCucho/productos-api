package com.reto.productos.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class ProductoUpdateDTO {

    @NotBlank(message = "El código del producto es obligatorio.")
    @Size(max = 20, message = "El código no puede superar los 20 caracteres.")
    private String codigo;

    @NotBlank(message = "El nombre del producto es obligatorio.")
    @Size(max = 120, message = "El nombre no puede superar los 120 caracteres.")
    private String nombre;

    @NotBlank(message = "La marca es obligatoria.")
    @Size(max = 60, message = "La marca no puede superar los 60 caracteres.")
    private String marca;

    @NotBlank(message = "El modelo es obligatorio.")
    @Size(max = 60, message = "El modelo no puede superar los 60 caracteres.")
    private String modelo;

    @NotNull(message = "El precio es obligatorio.")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo.")
    private BigDecimal precio;

    @NotNull(message = "El stock es obligatorio.")
    @Min(value = 0, message = "El stock no puede ser negativo.")
    private Integer stock;

    @NotBlank(message = "El estado es obligatorio.")
    @Size(max = 1, message = "El estado no puede superar los 1 caracteres.")
    private String estado;
}
