package com.reto.productos.dto;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductoCreateDTO {
    
    private String codigo;
    private String nombre;
    private String marca;
    private String modelo;
    private BigDecimal precio;
    private Integer stock;
}
