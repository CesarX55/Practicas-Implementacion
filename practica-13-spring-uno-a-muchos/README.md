# Spring Boot con JPA: relación uno a muchos

API REST para administrar productos y sus comentarios con Spring Boot, Spring Data JPA, Hibernate y PostgreSQL. Un producto tiene varios comentarios; cada comentario pertenece a un producto.

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

La URL de conexión usa el host `db` (Dev Container). `spring.jpa.hibernate.ddl-auto=update` deja que Hibernate actualice el esquema a partir de las entidades; es práctico para el ejercicio, pero en una aplicación real se administra con migraciones. También se configura `spring.jpa.open-in-view=false`.

## Estructura

```
practica-13-spring-uno-a-muchos/
├── .devcontainer/
├── demo/src/main/java/com/example/demo/
│   ├── DemoApplication.java
│   ├── model/        (Product, Comment)
│   ├── repo/         (productoRepo, comentarioRepo)
│   └── controllers/  (ProductoController, ComentarioController)
├── entregable.md     # Respuestas de la actividad
└── README.md
```

## Modelo y relación

- `Product` representa la tabla `producto` y contiene `Set<Comment> comentarios`.
- `Comment` representa la tabla `comentarios` y contiene la referencia `producto`.
- Los identificadores son `String` con `@GeneratedValue(strategy = GenerationType.UUID)`.
- Lombok: `@Getter`, `@Builder`, `@NoArgsConstructor` y `@AllArgsConstructor`; `@Setter` solo en los campos que los controladores modifican (identificadores, `Product.comentarios` y `Comment.producto`).

```java
// Product
@OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
private Set<Comment> comentarios;

// Comment
@ManyToOne(fetch = FetchType.LAZY)
private Product producto;
```

La llave foránea queda en la tabla de muchos (`comentarios`, normalmente en la columna `producto_id`) y la controla `Comment.producto`, el lado propietario. `mappedBy = "producto"` marca a `Product.comentarios` como el lado inverso. `@JsonManagedReference` y `@JsonBackReference` evitan la recursión infinita al convertir a JSON.

## Probar la API

1. Crear un producto (`POST /producto`):

```bash
curl --location 'http://localhost:8080/producto' \
  --header 'Content-Type: application/json' \
  --data '{"nombre":"Proteina Fresa 2.2 kg","descripcion":"Proteina de suero de leche en polvo","lote":"12345","precioUnitario":500.5,"fechaCaducidad":"2027-05-04"}'
```

2. Con el `id` UUID de la respuesta, crear un comentario (`POST /comentario/{productId}`):

```bash
curl --location 'http://localhost:8080/comentario/ID_DEL_PRODUCTO' \
  --header 'Content-Type: application/json' \
  --data '{"username":"usuario1","comentario_corto":"Buen producto","estrellas":4,"comentario_largo":"La entrega llegó a tiempo","imagen1":"https://example.com/imagen1.jpeg","imagen2":"https://example.com/imagen2.jpeg"}'
```

3. Consultar productos con sus comentarios, o los de un producto:

```bash
curl 'http://localhost:8080/productos'
curl 'http://localhost:8080/comentarios/ID_DEL_PRODUCTO'
```

| Método | Ruta | Acción |
|---|---|---|
| `GET` | `/productos` | Listar productos |
| `POST` | `/producto` | Crear un producto |
| `GET` / `PUT` / `DELETE` | `/producto/{id}` | Consultar, actualizar o eliminar un producto |
| `GET` | `/comentarios` | Listar comentarios |
| `POST` | `/comentario/{productId}` | Crear un comentario asociado a un producto |
| `GET` | `/comentarios/{productId}` | Comentarios de un producto |
| `GET` / `PUT` / `DELETE` | `/comentario/{id}` | Consultar, actualizar o eliminar un comentario |

## Actividad

Las respuestas de la actividad (llave foránea, `List` y `Set`, identificadores `String`, eliminación en cascada y tipos de carga) están en [`entregable.md`](entregable.md).

## Lista de comprobación

- [x] Las entidades `Product` y `Comment` declaran la relación en ambos lados
- [x] El endpoint `POST /comentario/{productId}` asigna el producto al comentario
- [x] `entregable.md` responde las preguntas de la actividad
- [ ] Probé la API y revisé en PostgreSQL la clave primaria y la llave foránea de `comentarios`
