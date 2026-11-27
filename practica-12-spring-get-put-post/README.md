# Spring Boot JPA: GET, PUT y POST

Práctica de Implementación de Sistemas: API REST con Spring Boot y JPA que crea, consulta, actualiza y elimina productos en PostgreSQL.

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

## Estructura

```
practica-12-spring-get-put-post/
├── .devcontainer/
├── demo/
│   ├── pom.xml, mvnw, mvnw.cmd
│   └── src/main/
│       ├── java/com/example/demo/
│       │   ├── DemoApplication.java
│       │   ├── model/Producto.java
│       │   ├── repo/ProductoRepo.java
│       │   └── producto/ProductoController.java
│       └── resources/application.properties
└── README.md
```

## Pasos

### Paso 1: base de datos

Crear el usuario `chochos` y la base `chochos` (ver *Entorno de desarrollo*).

### Paso 2: modelo `Producto`

Se usa Lombok (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`) en lugar de escribir getters, setters y constructores a mano. Campos: `id` (`Long`), `nombre`, `descripcion`, `lote`, `precioUnitario` (`BigDecimal`) y `fechaCaducidad` (`LocalDate`).

**¿Qué significa lo que está dentro de los paréntesis de `@GeneratedValue(strategy = GenerationType.IDENTITY)`?**
Indica la estrategia con la que se genera la clave primaria. `IDENTITY` delega la generación en la base de datos: la columna `id` es autoincremental (en PostgreSQL, una columna de identidad), de modo que la aplicación no asigna el valor y Hibernate lo lee después de hacer el `INSERT`. Otras estrategias son `SEQUENCE`, `AUTO` o `UUID`.

### Paso 3: método `GET /producto/{id}`

`ProductoController` usa el repositorio `ProductoRepo` (en `repo/ProductoRepo.java`, que extiende `JpaRepository<Producto, Long>`). El método `getById` devuelve `Optional<Producto>` con `repo.findById(id)`.

### Paso 4: insertar un registro (`POST /producto`)

```bash
curl -s -X POST localhost:8080/producto \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Creatina","descripcion":"Creatina de 500 g HoliXlab","lote":"12345","precioUnitario":500.5,"fechaCaducidad":"2026-05-04"}'
```

Respuesta esperada (el `id` lo asigna la base de datos):

```json
{"id":1,"nombre":"Creatina","descripcion":"Creatina de 500 g HoliXlab","lote":"12345","precioUnitario":500.5,"fechaCaducidad":"2026-05-04"}
```

### Paso 5: consultar el registro (`GET /producto/{id}`)

```bash
curl -s -X GET localhost:8080/producto/1
```

Devuelve el mismo JSON. También puedes abrir `http://localhost:8080/producto/1` en el navegador, o `http://localhost:8080/productos` para ver la lista.

### Paso 6: método `PUT /producto/{id}`

`putProduct` asigna el `id` de la ruta al producto recibido y lo guarda con `repo.save(...)`. Como el `id` ya existe, JPA hace una actualización en lugar de una inserción.

### Paso 7: actualizar un registro

```bash
curl -s -X PUT localhost:8080/producto/1 \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Proteína de fresa","descripcion":"Proteína de suero de leche 25 g, 2.2 kg","lote":"12345","precioUnitario":500.5,"fechaCaducidad":"2027-06-07"}'
```

Repite el Paso 5 para comprobar que los cambios se guardaron.

### Paso 8: método `DELETE /producto/{id}`

`deleteProduct` llama a `repo.deleteById(id)` y devuelve `{"success":true}`.

### Paso 9: validar la eliminación

```bash
curl -s -X DELETE localhost:8080/producto/1
curl -s localhost:8080/productos
```

Respuesta esperada: `{"success":true}` y después una lista vacía `[]`. Consultar el producto eliminado con `GET /producto/1` devuelve una respuesta vacía, porque el método devuelve un `Optional` vacío.

## Información útil

- Si cerraste la ventana mientras Spring seguía ejecutándose, busca el proceso con `fuser 8080/tcp` y termínalo con `kill <PID>`.
- Si el contenedor se reinició y PostgreSQL no está disponible, comprueba que el servicio `db` esté activo antes de iniciarlo manualmente.

## Lista de comprobación

- [x] `Producto` usa Lombok y `@GeneratedValue` con `IDENTITY`
- [x] Existen `GET /producto/{id}`, `PUT /producto/{id}` y `DELETE /producto/{id}`
- [X] Probé el POST, el GET, el PUT y el DELETE y obtuve las respuestas esperadas
