package com.reto.productos.repository;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

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

    // 3. Consulta Nativa Paginada con Filtros Opcionales (Mucho más limpio)
    @Query(
        value = "SELECT * FROM producto WHERE " +
                "(:marca IS NULL OR UPPER(marca) LIKE UPPER(CONCAT(CONCAT('%', :marca), '%'))) AND " +
                "(:modelo IS NULL OR UPPER(modelo) LIKE UPPER(CONCAT(CONCAT('%', :modelo), '%')))",
        countQuery = "SELECT count(*) FROM producto WHERE " +
                     "(:marca IS NULL OR UPPER(marca) LIKE UPPER(CONCAT(CONCAT('%', :marca), '%'))) AND " +
                     "(:modelo IS NULL OR UPPER(modelo) LIKE UPPER(CONCAT(CONCAT('%', :modelo), '%')))",
        nativeQuery = true
    )
    Page<ProductoEntity> listarConFiltrosNativos(
        @Param("marca") String marca,
        @Param("modelo") String modelo,
        Pageable pageable
    );
}
