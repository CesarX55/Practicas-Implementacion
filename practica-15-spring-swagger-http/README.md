# Documenta la API con Swagger y devuelve los códigos HTTP adecuados

Se amplía la API de Spring Boot (`demo/`) para documentar sus endpoints con OpenAPI/Swagger y para que devuelvan códigos de estado HTTP adecuados.

## Entorno de desarrollo

Abre esta carpeta en VS Code y elige **Reopen in Container**. El Dev Container levanta Java 21 (servicio `app`) y PostgreSQL 16 (servicio `db`, host `db`, puerto `5432`, usuario y contraseña iniciales `postgres`). Si ejecutas la aplicación fuera del contenedor, cambia `db` por `localhost` en `application.properties`.

Primero crea el usuario y la base de datos (una sola vez):

```bash
psql -h db -U postgres -d postgres
```

```sql
CREATE USER chochos WITH PASSWORD 'chochos';
CREATE DATABASE chochos OWNER chochos;
```

Para iniciar la aplicación:

```bash
cd demo
./mvnw spring-boot:run
```

Si `./mvnw` marca `Permission denied`, ejecuta `chmod +x mvnw` o usa `sh mvnw spring-boot:run`. Si necesitas liberar el puerto `8080`: `fuser 8080/tcp` y luego `kill <PID>`.

## Contexto

- Spring Boot 3.3.4 y Spring MVC.
- Controladores en `demo/src/main/java/com/example/demo/controllers/`.
- La aplicación se conecta a PostgreSQL con `application.properties`; verifica que la base esté disponible antes de iniciar.

## Parte 1: Swagger/OpenAPI

### Dependencia

`demo/pom.xml` incluye Springdoc en la versión `2.6.0`, compatible con Spring Boot 3.3.x. El starter configura todo automáticamente; no hace falta un servidor Swagger ni un controlador adicional.

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.6.0</version>
</dependency>
```

### Direcciones

- Swagger UI: <http://localhost:8080/swagger-ui/index.html>
- JSON de OpenAPI: <http://localhost:8080/v3/api-docs>

### Descripción de la API

Los controladores tienen `@Tag` y cada ruta tiene `@Operation` y `@ApiResponses` con sus códigos de respuesta. `OpenApiConfiguration` personaliza el título, la descripción y la versión de la API con un bean `OpenAPI`.

### ¿Cómo funciona con Spring Boot?

Spring Boot detecta los controladores administrados por Spring. Springdoc se integra con las rutas de Spring MVC (`@RestController`, `@GetMapping`, `@PostMapping`...), combina esa información con las anotaciones de OpenAPI y los modelos, y publica la especificación en `/v3/api-docs`. Swagger UI lee esa especificación y la muestra como documentación interactiva. Desde ahí se pueden enviar solicitudes a la aplicación en ejecución; Swagger UI no implementa los endpoints ni cambia su comportamiento.

## Parte 2: códigos de estado HTTP

Los controladores devuelven `ResponseEntity<T>`, que permite definir el cuerpo y el código de la respuesta. Un método que devuelve directamente un objeto Java solo expresa `200`.

| Solicitud | Resultado |
|---|---|
| `GET /productos` | `200 OK`; la lista, aunque esté vacía (`[]`) |
| `GET /producto/{id}` existente | `200 OK` con el producto |
| `GET /producto/{id}` inexistente | `404 Not Found` |
| `POST /producto` válido | `201 Created`, el producto guardado y el encabezado `Location: /producto/{id}` |
| `PUT /producto/{id}` existente | `200 OK` con el producto actualizado |
| `PUT /producto/{id}` inexistente | `404 Not Found`; no se crea automáticamente |
| `DELETE /producto/{id}` existente | `204 No Content` |
| `DELETE /producto/{id}` inexistente | `404 Not Found` |

El mismo criterio se aplica a categorías, comentarios e imágenes: los recursos o padres inexistentes devuelven `404` y no `null`. Se conservaron las rutas existentes (`/producto` y `/productos`).

Cambios de comportamiento a destacar:

- `PUT` y `DELETE` comprueban con `existsById` antes de actuar.
- El `PUT` de categorías conserva los productos asociados: como `Department` es el lado propietario de la relación, guardar una categoría con el conjunto vacío borraría las asociaciones.

## Parte 3: verificar el comportamiento

```bash
# Crear un producto: 201 Created y encabezado Location
curl -i --header 'Content-Type: application/json' \
  --data '{"nombre":"Proteína de fresa","descripcion":"2.2 kg","lote":"123456","precioUnitario":1500.5,"fechaCaducidad":"2026-05-04"}' \
  http://localhost:8080/producto

# Búsqueda exitosa (200) e inexistente (404)
curl -i http://localhost:8080/producto/EXISTING_ID
curl -i http://localhost:8080/producto/does-not-exist

# Lista (200)
curl -i http://localhost:8080/productos

# Eliminar: 204 y después 404
curl -i -X DELETE http://localhost:8080/producto/EXISTING_ID
curl -i -X DELETE http://localhost:8080/producto/EXISTING_ID
```

Después abre Swagger UI y confirma que muestra las operaciones de productos con los códigos de respuesta documentados, y consulta `/v3/api-docs` para revisar el documento generado.

## Lista de comprobación

- [x] Springdoc usa una versión compatible con Spring Boot 3.3.x (`2.6.0`)
- [x] Los endpoints tienen resúmenes y códigos de respuesta documentados
- [x] La creación devuelve `201` y el encabezado `Location`
- [x] Los recursos inexistentes devuelven `404`, no `null`
- [x] Las eliminaciones exitosas devuelven `204` sin cuerpo
- [x] Se conservan las rutas existentes
- [ ] Swagger UI y `/v3/api-docs` cargan con la aplicación en ejecución
- [ ] Probé creación, búsqueda, actualización y eliminación con IDs existentes e inexistentes
