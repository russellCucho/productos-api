package com.reto.productos.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ProductoTabularDTO {
    private Long idProducto;
    private String codigo;
    private String nombre;
    private String marca;
    private String modelo;
    private BigDecimal precio;
    private Integer stock;
    private String estado;
}
