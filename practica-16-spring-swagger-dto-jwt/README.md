# Proyecto: Swagger, DTOs, códigos HTTP y seguridad JWT

API REST de productos, categorías, comentarios e imágenes con documentación OpenAPI, DTOs validados, respuestas HTTP adecuadas y autorización por roles con JWT. Spring Boot 3.3.4, Java 21, Spring MVC, Spring Data JPA y PostgreSQL.

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

## Política de acceso

Todos los endpoints `GET` son públicos. Las escrituras requieren rol, excepto el registro y el inicio de sesión.

| Recurso | Escrituras (POST, PUT, PATCH, DELETE) |
|---|---|
| Productos y categorías | Solo `ADMIN` |
| Comentarios e imágenes | `USER` o `ADMIN` |
| `POST /auth/register`, `POST /auth/login` | Públicos |

## Estructura

```
practica-16-spring-swagger-dto-jwt/
├── .devcontainer/
└── demo/
    ├── pom.xml, mvnw, mvnw.cmd
    └── src/
        ├── main/java/com/example/demo/
        │   ├── DemoApplication.java
        │   ├── OpenApiConfiguration.java    # Info de la API y esquema Bearer JWT
        │   ├── SecurityConfig.java          # Resource Server, filtros y beans de JWT
        │   ├── dto/          # Request y Response de producto, categoría, comentario, imagen y autenticación
        │   ├── model/        # Producto, Department, Comment, Image, AppUser
        │   ├── repo/         # productoRepo, categoriaRepo, comentarioRepo, imagenRepo, appUserRepo
        │   ├── service/      # JwtTokenService
        │   └── controllers/  # Producto, Categoria, Comentario, Imagen, Auth
        ├── main/resources/application.properties
        └── test/java/com/example/demo/ApiIntegrationTests.java
```

## Qué se implementó

1. **Swagger y OpenAPI:** Springdoc `2.6.0`, `OpenApiConfiguration` con esquema `bearerAuth` (HTTP Bearer, formato JWT), `@Tag` y `@Operation` en cada controlador y `@SecurityRequirements` vacío en los GET públicos para que Swagger UI no pida token ahí.
2. **Códigos HTTP:** todos los controladores devuelven `ResponseEntity` con `200`, `201` (con `Location`), `204`, `400`, `404`, y `401`/`403` por seguridad.
3. **DTOs con validación:** el cliente nunca controla el ID generado ni las relaciones JPA. Los `Request` usan Bean Validation (`@NotBlank`, `@NotNull`, `@PositiveOrZero`, `@Min`/`@Max`) y los `Response` exponen solo los campos públicos.
4. **PUT y PATCH:** `PUT` reemplaza todos los campos editables y responde `404` si el recurso no existe. `PATCH /producto/{id}` (con `ProductoPatchRequest`) actualiza solo los campos enviados; un PATCH vacío devuelve `400`. Con esta estrategia, `null` significa "no enviado", así que no se pueden borrar campos enviando `null`.
5. **Cuentas persistidas:** entidad `AppUser` (tabla `app_users`) con `username` único, `passwordHash` y `role`; repositorio `appUserRepo.findByUsername`.
6. **Registro, login y JWT:** `POST /auth/register` (contraseña con BCrypt, rol siempre `USER`, `409` si el usuario existe) y `POST /auth/login` (`AuthenticationManager`, `401` si las credenciales son inválidas). `JwtTokenService` firma con HS256 los claims `sub`, `roles`, `iat`, `exp` (1 hora) e `iss`.
7. **Resource Server y permisos:** `SecurityConfig` con `@EnableMethodSecurity`, sesiones stateless, `JwtDecoder`/`JwtEncoder` y un conversor que lee el claim `roles` y agrega el prefijo `ROLE_`. `@PreAuthorize` por clase según la política y `@PreAuthorize("permitAll()")` en cada GET.
8. **Ruta de imágenes corregida:** la lista de un producto pasó a `GET /imagenes/producto/{productId}`, para no chocar con `GET /imagen/{id}`.
9. **Pruebas de integración:** `ApiIntegrationTests` cubre los casos de la lista del paso 11.

## Clave JWT

La clave de firma se lee de la variable de entorno `DEMO_JWT_SECRET` (propiedad `demo.security.jwt-secret`): una clave aleatoria codificada en Base64 de al menos 32 bytes. No la confundas con contraseñas de usuario ni la subas al repositorio. Si no se define, la aplicación genera una clave aleatoria al iniciar (útil para desarrollo, pero los tokens dejan de servir al reiniciar). Para conservarlos durante pruebas locales:

```bash
export DEMO_JWT_SECRET="$(openssl rand -base64 32)"
```

## Endpoints

| Método | Ruta | Acceso |
|---|---|---|
| `POST` | `/auth/register`, `/auth/login` | Público |
| `GET` | `/productos`, `/producto/{id}` | Público |
| `POST` / `PUT` / `PATCH` / `DELETE` | `/producto`, `/producto/{id}` | `ADMIN` |
| `GET` | `/categorias`, `/categoria/{id}`, `/categoria/producto/{productId}` | Público |
| `POST` / `PUT` / `DELETE` | `/categoria`, `/categoria/{id}`, `/addProductoCategoria/{productId}/{categoriaId}` | `ADMIN` |
| `GET` | `/comentarios`, `/comentarios/{productId}`, `/comentario/{id}` | Público |
| `POST` / `PUT` / `DELETE` | `/comentario`, `/comentario/{productId}`, `/comentario/{id}` | `USER` o `ADMIN` |
| `GET` | `/imagenes`, `/imagenes/producto/{productId}`, `/imagen/{id}` | Público |
| `POST` / `PUT` / `DELETE` | `/imagen`, `/imagen/{productId}`, `/imagen/{id}` | `USER` o `ADMIN` |

Documentación: <http://localhost:8080/swagger-ui/index.html> y <http://localhost:8080/v3/api-docs>.

## Probar manualmente

Registrar una cuenta `USER` e iniciar sesión:

```bash
curl -i -X POST http://localhost:8080/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"estudiante","password":"clave-local-segura-123"}'

curl -i -X POST http://localhost:8080/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"estudiante","password":"clave-local-segura-123"}'
```

Usar el `accessToken` devuelto:

```bash
curl -i http://localhost:8080/imagenes -H 'Authorization: Bearer PEGA_AQUI_EL_TOKEN'
```

Casos a comprobar:

1. Cada `GET` funciona sin token; los IDs inexistentes devuelven `404` (no `401`) y las colecciones vacías devuelven `200` con `[]`.
2. `POST /producto` sin token devuelve `401`.
3. `POST /producto` con un token `USER` devuelve `403`.
4. Un token `USER` puede escribir comentarios e imágenes con un cuerpo válido.
5. Para probar permisos `ADMIN`, cambia el rol de una cuenta con un proceso administrativo confiable en la base de datos y vuelve a iniciar sesión (no existe una ruta pública de promoción de usuarios):

   ```sql
   UPDATE app_users SET role = 'ADMIN' WHERE username = 'nombre-de-la-cuenta';
   ```

6. Entradas inválidas devuelven `400`; la creación devuelve `201` con `Location`; la actualización `200`; la eliminación `204`.

## Pruebas automáticas

```bash
cd demo
./mvnw test
```

`ApiIntegrationTests` requiere PostgreSQL disponible con la base `chochos`, y comprueba que:

- todos los `GET` responden sin token y los IDs inexistentes dan `404`;
- una escritura protegida sin token o con un token falso responde `401`;
- un token de `USER` recibe `403` al escribir productos y categorías;
- `USER` y `ADMIN` pueden escribir comentarios e imágenes;
- el registro siempre crea el rol `USER` y rechaza duplicados con `409`;
- el login emite un JWT válido y credenciales incorrectas responden `401`;
- un DTO inválido responde `400`;
- `PATCH` modifica solo los campos enviados;
- OpenAPI publica el esquema Bearer JWT.

## Lista de comprobación

- [x] Springdoc, `OpenApiConfiguration` y el esquema `bearerAuth` están configurados
- [x] Los controladores usan DTOs en lugar de entidades JPA en sus contratos
- [x] `PUT` reemplaza campos editables y `PATCH` conserva los no enviados
- [x] Todos los GET son públicos y las escrituras conservan autorización por rol
- [x] El registro crea solo `USER` y el login emite un JWT Bearer
- [x] La clave JWT se lee de una variable de entorno, no del código
- [x] No hay mappings GET duplicados para imágenes
- [ ] Swagger UI y `/v3/api-docs` cargan
- [ ] `./mvnw test` pasa
