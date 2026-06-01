-- Asegurar el esquema limpio de trabajo en Oracle
ALTER SESSION SET CURRENT_SCHEMA = C##RETO_TISMART;

-- 1. Especificación del Paquete
CREATE OR REPLACE PACKAGE pkg_producto AS

    -- Crear producto (Valida negocio y retorna el ID autogenerado)
    PROCEDURE sp_crear_producto (
        p_codigo IN VARCHAR2,
        p_nombre IN VARCHAR2,
        p_marca  IN VARCHAR2,
        p_modelo IN VARCHAR2,
        p_precio IN NUMBER,
        p_stock  IN NUMBER,
        p_id     OUT NUMBER
    );

    -- Obtener producto por ID (Usa SYS_REFCURSOR para el detalle de auditoría)
    PROCEDURE sp_obtener_producto_id (
        p_id_producto IN NUMBER,
        p_cursor      OUT SYS_REFCURSOR
    );

    -- Eliminar lógico (Cambia estado a 'I' y actualiza fecha_modif)
    PROCEDURE sp_eliminar_logico_producto (
        p_id_producto IN NUMBER
    );

END pkg_producto;
/

-- 2. Cuerpo del Paquete (Lógica de Servidor)
CREATE OR REPLACE PACKAGE BODY pkg_producto AS

    -- Implementación de Crear Producto con Reglas de Negocio
    PROCEDURE sp_crear_producto (
        p_codigo IN VARCHAR2,
        p_nombre IN VARCHAR2,
        p_marca  IN VARCHAR2,
        p_modelo IN VARCHAR2,
        p_precio IN NUMBER,
        p_stock  IN NUMBER,
        p_id     OUT NUMBER
    ) AS
    BEGIN
        -- Regla de negocio de respaldo en base de datos: Precio >= 0
        IF p_precio < 0 THEN
            RAISE_APPLICATION_ERROR(-20001, 'El precio no puede ser negativo.');
        END IF;

        -- Regla de negocio de respaldo en base de datos: Stock >= 0
        IF p_stock < 0 THEN
            RAISE_APPLICATION_ERROR(-20002, 'El stock no puede ser negativo.');
        END IF;

        -- Inserción limpia (La restricción UNIQUE uk_producto_codigo protegerá el código único)
        INSERT INTO producto (codigo, nombre, marca, modelo, precio, stock, estado, fecha_creacion)
        VALUES (p_codigo, p_nombre, p_marca, p_modelo, p_precio, p_stock, 'A', SYSTIMESTAMP)
        RETURNING id_producto INTO p_id;
        
        COMMIT;
    END sp_crear_producto;

    -- Implementación de Obtener por ID abriendo el SYS_REFCURSOR
    PROCEDURE sp_obtener_producto_id (
        p_id_producto IN NUMBER,
        p_cursor      OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
        SELECT id_producto, codigo, nombre, marca, modelo, precio, stock, estado, fecha_creacion, fecha_modif
        FROM producto
        WHERE id_producto = p_id_producto;
    END sp_obtener_producto_id;

    -- Implementación de Borrado Lógico
    PROCEDURE sp_eliminar_logico_producto (
        p_id_producto IN NUMBER
    ) AS
    BEGIN
        UPDATE producto
        SET estado = 'I',
            fecha_modif = SYSTIMESTAMP
        WHERE id_producto = p_id_producto;
        
        COMMIT;
    END sp_eliminar_logico_producto;

END pkg_producto;
/