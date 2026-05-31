package com.reto.productos.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import com.reto.productos.entity.ProductoEntity;

public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {
    // 1. Mapeo para el SP_CREAR_PRODUCTO (Captura el OUT NUMBER de Oracle como Long)
    @Procedure(procedureName = "PKG_PRODUCTO.SP_CREAR_PRODUCTO", outputParameterName = "p_id")
    Long spCrearProducto(
        @Param("p_codigo") String codigo,
        @Param("p_nombre") String nombre,
        @Param("p_marca") String marca,
        @Param("p_modelo") String modelo,
        @Param("p_precio") BigDecimal precio,
        @Param("p_stock") Integer stock
    );

    // 2. Mapeo para el SP_ELIMINAR_LOGICO_PRODUCTO
    @Procedure(procedureName = "PKG_PRODUCTO.SP_ELIMINAR_LOGICO_PRODUCTO")
    void spEliminarLogicoProducto(@Param("p_id_producto") Long idProducto);
}
