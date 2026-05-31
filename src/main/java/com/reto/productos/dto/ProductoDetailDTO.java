package com.reto.productos.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class ProductoDetailDTO {

    private Long idProducto;
    private String codigo;
    private String nombre;
    private String marca;
    private String modelo;
    private BigDecimal precio;
    private Integer stock;
    private String estado;
    private Date fechaCreacion;
    private Date fechaModif;
}
