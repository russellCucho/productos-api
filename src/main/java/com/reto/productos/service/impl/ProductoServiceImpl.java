package com.reto.productos.service.impl;

import com.reto.productos.dto.ProductoCreateDTO;
import com.reto.productos.dto.ProductoDetailDTO;
import com.reto.productos.dto.ProductoTabularDTO;
import com.reto.productos.dto.ProductoUpdateDTO;
import com.reto.productos.entity.ProductoEntity;
import com.reto.productos.exception.CustomException;
import com.reto.productos.repository.ProductoRepository;
import com.reto.productos.service.ProductoService;
import com.reto.productos.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;
import javax.persistence.StoredProcedureQuery;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final EntityManager entityManager; // Inyectado para abrir el SYS_REFCURSOR de Oracle

    @Override
    @Transactional(readOnly = true)
    public List<ProductoTabularDTO> listarTodos() {
        // Usamos el JpaRepository convencional para traer la lista optimizada para la
        // tabla
        return productoRepository.findAll().stream()
                .map(p -> ProductoTabularDTO.builder()
                        .idProducto(p.getIdProducto())
                        .codigo(p.getCodigo())
                        .nombre(p.getNombre())
                        .marca(p.getMarca())
                        .modelo(p.getModelo())
                        .precio(p.getPrecio())
                        .stock(p.getStock())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public ProductoDetailDTO obtenerPorId(Long id) {
        // Llamada manual al SYS_REFCURSOR usando el query registrado en la entidad
        StoredProcedureQuery query = entityManager
                .createNamedStoredProcedureQuery("ProductoEntity.spObtenerProductoId");
        query.setParameter("p_id_producto", id);

        query.execute();
        List<ProductoEntity> resultado = query.getResultList();

        if (resultado.isEmpty()) {
            throw new CustomException(Constants.MSG_PRODUCTO_NOT_FOUND, HttpStatus.NOT_FOUND, Constants.SEVERITY_ERROR);
        }

        ProductoEntity p = resultado.get(0);
        return ProductoDetailDTO.builder()
                .idProducto(p.getIdProducto())
                .codigo(p.getCodigo())
                .nombre(p.getNombre())
                .marca(p.getMarca())
                .modelo(p.getModelo())
                .precio(p.getPrecio())
                .stock(p.getStock())
                .estado(p.getEstado())
                .fechaCreacion(p.getFechaCreacion())
                .fechaModif(p.getFechaModif())
                .build();
    }

    @Override
    @Transactional
    public Long crear(ProductoCreateDTO dto) {
        // Validaciones básicas de negocio usando nuestras constantes
        if (dto.getPrecio().signum() < 0) {
            throw new CustomException(Constants.MSG_PRECIO_NEGATIVO, HttpStatus.BAD_REQUEST, Constants.SEVERITY_ERROR);
        }
        if (dto.getStock() < 0) {
            throw new CustomException(Constants.MSG_STOCK_NEGATIVO, HttpStatus.BAD_REQUEST, Constants.SEVERITY_ERROR);
        }

        // Ejecuta el SP y retorna el ID autogenerado
        return productoRepository.spCrearProducto(
                dto.getCodigo(),
                dto.getNombre(),
                dto.getMarca(),
                dto.getModelo(),
                dto.getPrecio(),
                dto.getStock());
    }

    @Override
    @Transactional
    public void actualizar(Long id, ProductoUpdateDTO dto) {
        // Primero verificamos si existe consumiendo el SP del cursor
        this.obtenerPorId(id);

        if (dto.getPrecio().signum() < 0) {
            throw new CustomException(Constants.MSG_PRECIO_NEGATIVO, HttpStatus.BAD_REQUEST, Constants.SEVERITY_ERROR);
        }
        if (dto.getStock() < 0) {
            throw new CustomException(Constants.MSG_STOCK_NEGATIVO, HttpStatus.BAD_REQUEST, Constants.SEVERITY_ERROR);
        }

        // Mapeo manual de actualización sobre el Entity manejado por JPA
        ProductoEntity entity = productoRepository.findById(id).get();
        entity.setCodigo(dto.getCodigo());
        entity.setNombre(dto.getNombre());
        entity.setMarca(dto.getMarca());
        entity.setModelo(dto.getModelo());
        entity.setPrecio(dto.getPrecio());
        entity.setStock(dto.getStock());
        entity.setEstado(dto.getEstado());
        entity.setFechaModif(new java.util.Date());

        productoRepository.save(entity);
    }

    @Override
    @Transactional
    public void eliminarLogico(Long id) {
        // Verificamos existencia previa
        this.obtenerPorId(id);
        // Llama al SP encargado del borrado lógico cambiando el estado a 'I'
        productoRepository.spEliminarLogicoProducto(id);
    }

    // Nuevo método: Consulta Nativa Paginada con Filtros (Limpio de paquetes largos)
    @Override
    @Transactional(readOnly = true)
    public Page<ProductoTabularDTO> listarPaginado(String marca, String modelo, int page, int size) {
        // 1. Creamos el objeto de paginación (ID descendente para ver lo último creado primero)
        Pageable pageable = PageRequest.of(
                page, 
                size, 
                Sort.by("ID_PRODUCTO").descending()
        );

        // 2. Limpiamos los hilos vacíos para pasarlos como NULL a Oracle si vienen en blanco desde el cliente
        String filtroMarca = (marca != null && !marca.trim().isEmpty()) ? marca.trim() : null;
        String filtroModelo = (modelo != null && !modelo.trim().isEmpty()) ? modelo.trim() : null;

        // 3. Ejecutamos la Query Nativa paginada
        Page<ProductoEntity> entidadesPaginadas = 
                productoRepository.listarConFiltrosNativos(filtroMarca, filtroModelo, pageable);

        // 4. Transformamos la página de entidades a una página de DTOs
        return entidadesPaginadas.map(p -> ProductoTabularDTO.builder()
                .idProducto(p.getIdProducto())
                .codigo(p.getCodigo())
                .nombre(p.getNombre())
                .marca(p.getMarca())
                .modelo(p.getModelo())
                .precio(p.getPrecio())
                .stock(p.getStock())
                .build());
    }
}