package com.reto.productos.service;

import com.reto.productos.dto.ProductoCreateDTO;
import com.reto.productos.dto.ProductoDetailDTO;
import com.reto.productos.dto.ProductoTabularDTO;
import com.reto.productos.dto.ProductoUpdateDTO;
import org.springframework.data.domain.Page;
import java.util.List;

public interface ProductoService {
    
    List<ProductoTabularDTO> listarTodos();
    ProductoDetailDTO obtenerPorId(Long id);
    Long crear(ProductoCreateDTO dto);
    void actualizar(Long id, ProductoUpdateDTO dto);
    void eliminarLogico(Long id);
    
    Page<ProductoTabularDTO> listarPaginado(String marca, String modelo, int page, int size);
}
