#  Backend API (Java 8 & Spring Boot)

## Arquitectura y Tecnologías
* **Java Version:** 8.
* **Framework:** Spring Boot 2.7.18
* **Gestor de Dependencias:** Maven.
* **Base de Datos:** Oracle Database Enterprise Edition 12c (12.2.0.1).

---

## Requisito Obligatorio antes de iniciar (Licencia de Oracle)

La imagen utilizada en este proyecto pertenece al registro oficial de Oracle corporativo (`container-registry.oracle.com`). Por políticas de licenciamiento, **Docker no permitirá descargar la imagen automáticamente si no se aceptan previamente sus términos en la web.**

### Pasos obligatorios para habilitar la descarga:
1. Entra al navegador web e ingresa a: [Oracle Container Registry](https://container-registry.oracle.com/)
2. Inicia sesión con tu cuenta de Oracle (si no tienes una, créala).
3. Busca la sección **Database** y luego haz clic en **enterprise**.
4. Haz clic en el botón de **Continue** ubicado a la derecha (color azul) y luego aceptas las condiciones.
5. Abre la terminal de tu máquina anfitriona y ejecuta el comando de autenticación de Docker para enlazar tu cuenta local con el servidor de Oracle:
   ```bash
   docker login https://container-registry.oracle.com
    ```
6. Genera un token de autenticacion dandole click en tu usuario de arriba a la derecha en el navbar de la pagina de oracle y luego presionas 'Auth Token' del menu desplegable, guarda ese token (sera tu contrasenha).

7. Ingresa el correo y tu token de ingreso de tu cuenta de Oracle (contrasenha NO). 


### Inicialización de la Base de Datos con Docker:
Una vez que hayas iniciado sesión en el registro de Oracle desde tu terminal, ya puedes levantar el contenedor aislado.
1. Ubícate en la raíz de este proyecto (donde se encuentra el archivo docker-compose.yml).
3. Ejecuta el comando de construcción en segundo plano:

   ```bash
   docker compose up -d
    ```
4. Nota: El motor de Oracle Enterprise tarda entre 3 y 5 minutos en levantar sus servicios internos de base de datos por primera vez en segundo plano. Puedes verificar que el búnker esté operativo monitoreando los logs del contenedor:


   ```bash
   docker logs -f oracle_12c_enterprise
    ```

### Configuración Obligatoria de Usuario
Por defecto, el contenedor de Oracle levanta limpio. Para poder conectarte desde DBeaver e instalar las tablas, debes crear manualmente el usuario con permisos de administrador ejecutando los siguientes comandos en la consola interna del contenedor.

Abre una terminal en tu máquina anfitriona y ejecuta los siguientes comandos en orden:

1. Ingresa a la consola interactiva de SQL*Plus dentro del contenedor como SYSDBA:**
   ```bash
   docker exec -it oracle_12c_enterprise /bin/bash -c '$ORACLE_HOME/bin/sqlplus / as sysdba'
    ```

2. Una vez dentro de la consola de `SQL>`, ejecuta en orden el siguiente bloque de comandos:

    ```sql
    -- Unificamos la contraseña del administrador del sistema por defecto
    ALTER USER system IDENTIFIED BY oracle;

    -- Activamos el flag para permitir la creación de usuarios comunes en la raíz (C##)
    ALTER SESSION SET "_ORACLE_SCRIPT"= TRUE;

    -- Creamos el espacio limpio de trabajo para el reto
    CREATE USER c##reto_tismart IDENTIFIED BY oracle DEFAULT TABLESPACE "USERS" TEMPORARY TABLESPACE "TEMP";

    -- Le asignamos cuota ilimitada y los superpoderes de desarrollo requeridos
    ALTER USER c##reto_tismart QUOTA UNLIMITED ON USERS;
    GRANT CREATE SESSION TO c##reto_tismart;
    GRANT "RESOURCE" TO c##reto_tismart;
    ALTER USER c##reto_tismart DEFAULT ROLE "RESOURCE";

    -- Salimos de la consola de SQL*Plus
    exit;
    ```

### Datos de Conexión (DBeaver por ejemplo)
Una vez creado el usuario, abre tu gestor de base de datos preferido (como DBeaver) y configúralo con estos parámetros:

* **Host:** localhost
* **Puerto:** 1521
* **SID:** ORCLCDB
* **Username:** c##reto_tismart
* **Password:** oracle

### Estructura de Datos y PL/SQL

Los scripts SQL ordenados para poblar y estructurar tu entorno se encuentran organizados dentro de los recursos del proyecto en `src/main/resources/db/migration/`. Ejecútalos en tu cliente SQL (DBeaver) respetando el siguiente orden estricto:

1. `1_TABLA_PRODUCTO.sql`: Ejecuta la creación de la tabla física PRODUCTO

2. `2_PKG_PRODUCTO.sql`: Compila la especificación y el cuerpo del PKG_PRODUCTO. Con los siguientes:
* **sp_crear_producto**
* **sp_eliminar_logico_producto**
* **sp_obtener_producto_id**


### Configuración de Credenciales y Propiedades
En el directorio `src/main/resources/` encontrarás una plantilla de configuración llamada `application.template.properties`. Para que el proyecto conecte correctamente con tu entorno local:
1. Crea una copia de ese archivo en la misma ruta y renombralo como `application.properties`.
2. Asegúrate de configurar las variables de entorno de base de datos con las credenciales reales:
* spring.datasource.username=
* spring.datasource.password=

### Ejecución del Proyecto Backend
* **Opción 1 (IDE):** Abre el proyecto en tu IDE preferido, ve al archivo principal `ProductosApplication.java` y dale a ejecutar (Run).

El backend estará listo y escuchando en `http://localhost:8080`.


### Catálogo de Endpoints REST Expuestos

| Método | Endpoint | Descripción | Body / DTO Esperado | Códigos de Respuesta |
| :--- | :--- | :--- | :--- | :--- |
| **POST** | `/api/productos` | Registra un nuevo producto usando el SP. | `ProductoCreateDTO` | `201 Created`, `400 Bad Request`, `409 Conflict` |
| **GET** | `/api/productos` | Consulta paginada con filtros LIKE opcionales (`?marca=&modelo=&page=&size=`), por defecto page es 0 y size es 5. | *Ninguno (Parámetros de URL)* | `200 OK` |
| **GET** | `/api/productos/{id}` | Recupera buscar producto por id. | *Ninguno* | `200 OK`, `404 Not Found` |
| **PUT** | `/api/productos/{id}` | Modifica producto. | `ProductoUpdateDTO` | `204 No Content`, `400 Bad Request`, `404 Not Found` |
| **DELETE** | `/api/productos/{id}` | Ejecuta el borrado lógico. | *Ninguno* | `204 No Content`, `404 Not Found` |

