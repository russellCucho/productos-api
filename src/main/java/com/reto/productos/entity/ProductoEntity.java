package com.reto.productos.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "PRODUCTO")
@NamedStoredProcedureQuery(
    name = "ProductoEntity.spObtenerProductoId",
    procedureName = "PKG_PRODUCTO.SP_OBTENER_PRODUCTO_ID",
    resultClasses = ProductoEntity.class,
    parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_id_producto", type = Long.class),
        @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, name = "p_cursor", type = void.class)
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PRODUCTO")
    private Long idProducto;

    @Column(name = "CODIGO", nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(name = "NOMBRE", nullable = false, length = 120)
    private String nombre;

    @Column(name = "MARCA", nullable = false, length = 60)
    private String marca;

    @Column(name = "MODELO", nullable = false, length = 60)
    private String modelo;

    @Column(name = "PRECIO", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "STOCK", nullable = false)
    private Integer stock;

    @Column(name = "ESTADO", nullable = false, length = 1, columnDefinition = "CHAR(1)")
    private String estado;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_CREACION", nullable = false, updatable = false)
    private Date fechaCreacion;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_MODIF")
    private Date fechaModif;

    // Gancho interceptor antes de insertar en la base de datos
    @PrePersist
    protected void onCreate() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = new Date();
        }
        if (this.estado == null) {
            this.estado = "A"; // Todo producto nuevo nace Activo
        }
    }
}
