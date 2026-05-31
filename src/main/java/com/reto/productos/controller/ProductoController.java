package com.reto.productos.controller;

import com.reto.productos.dto.ProductoCreateDTO;
import com.reto.productos.dto.ProductoDetailDTO;
import com.reto.productos.dto.ProductoTabularDTO;
import com.reto.productos.dto.ProductoUpdateDTO;
import com.reto.productos.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {
        RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE })
public class ProductoController {

    private final ProductoService productoService;

    // 1. GET: Listar todos los productos (Data optimizada para tablas)
    @GetMapping
    public ResponseEntity<List<ProductoTabularDTO>> listarTodos() {
        List<ProductoTabularDTO> productos = productoService.listarTodos();
        return ResponseEntity.ok(productos);
    }

    // 2. GET: Obtener el detalle completo de un producto por ID (Consume el
    // SYS_REFCURSOR)
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDetailDTO> obtenerPorId(@PathVariable Long id) {
        ProductoDetailDTO producto = productoService.obtenerPorId(id);
        return ResponseEntity.ok(producto);
    }

    // 3. POST: Crear un nuevo producto (Retorna el ID autogenerado por el SP)
    @PostMapping
    public ResponseEntity<Long> crear(@Valid @RequestBody ProductoCreateDTO dto) {
        Long idGenerado = productoService.crear(dto);
        return new ResponseEntity<>(idGenerado, HttpStatus.CREATED);
    }

    // 4. PUT: Actualizar un producto existente
    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Long id,
            @Valid @RequestBody ProductoUpdateDTO dto) {
        productoService.actualizar(id, dto);
        return ResponseEntity.noContent().build();
    }

    // 5. DELETE: Borrado lógico (Pasa el estado a 'I' vía SP)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLogico(@PathVariable Long id) {
        productoService.eliminarLogico(id);
        return ResponseEntity.noContent().build();
    }
}