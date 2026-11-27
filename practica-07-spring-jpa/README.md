# Práctica: Spring Boot con JPA y PostgreSQL

API REST con Spring Boot que guarda y consulta datos en PostgreSQL mediante JPA, ejecutándose dentro de un Dev Container con Java 21.

## Entorno de desarrollo

Abre esta carpeta (`practica-07-spring-jpa`) en VS Code y elige **Reopen in Container**. El Dev Container levanta dos servicios:

- `app`: Java 21 y Maven Wrapper, donde se ejecuta la aplicación.
- `db`: PostgreSQL 16, accesible desde `app` con el host `db` y el puerto `5432`. Inicialmente usa la base de datos y las credenciales `postgres`.

Para iniciar la aplicación:

```bash
cd demo
./mvnw spring-boot:run
```

VS Code reenvía el puerto `8080` de Spring Boot. El puerto `5432` de PostgreSQL también se reenvía (`"db:5432"` en `devcontainer.json`), por lo que puedes conectarte desde una herramienta del equipo local usando `localhost`.

## Estructura

```
practica-07-spring-jpa/
├── .devcontainer/
│   ├── devcontainer.json
│   └── docker-compose.yml
├── demo/
│   ├── .mvn/wrapper/maven-wrapper.properties
│   ├── mvnw, mvnw.cmd
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/example/demo/
│       │   ├── DemoApplication.java
│       │   ├── model/        (Producto, Libro)
│       │   ├── repo/         (ProductoRepository, LibroRepository)
│       │   ├── producto/     (ProductoController)
│       │   └── libro/        (LibroController)
│       └── resources/application.properties
├── .gitattributes
├── .gitignore
└── README.md
```

## Conceptos básicos

**¿Qué es JPA?** JPA (Java Persistence API) es una especificación de Java para guardar, consultar, actualizar y eliminar datos usando objetos en lugar de escribir cada consulta SQL a mano. Las clases del modelo se relacionan con tablas mediante anotaciones como `@Entity` y `@Id`. Hibernate es una implementación de JPA y Spring Data JPA facilita su uso con repositorios como `JpaRepository`.

**¿Qué es un controlador REST?** Una clase que expone operaciones de la aplicación mediante URL y métodos HTTP como `GET` y `POST`. En Spring, `@RestController` indica que sus métodos atienden solicitudes web y que sus resultados se devuelven normalmente como JSON.

## Pasos

### Paso 1: crear la base de datos

Desde una terminal del Dev Container:

```bash
psql -h db -U postgres -d postgres
```

La contraseña es `postgres`. En la consola de PostgreSQL:

```sql
CREATE USER chochos WITH PASSWORD 'chochos';
CREATE DATABASE chochos OWNER chochos;
```

Sal con `\q`. Si ejecutas la aplicación en el equipo local, usa `localhost` en lugar de `db`.

### Paso 2: agregar dependencias

En `demo/pom.xml` se incluyen `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, el driver `postgresql` (scope `runtime`) y `lombok` 1.18.34 (scope `provided`).

### Paso 3: configurar la conexión

`demo/src/main/resources/application.properties`:

```properties
spring.application.name=demo
spring.datasource.url=jdbc:postgresql://db:5432/chochos
spring.datasource.username=chochos
spring.datasource.password=chochos
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

Fuera del Dev Container, cambia `db` por `localhost`. `ddl-auto=update` permite que Hibernate actualice el esquema a partir de las entidades.

### Paso 4: crear la entidad

`model/Producto.java`, con `@Entity`, `@Table(name = "producto")`, `@Id` y las anotaciones de Lombok `@Data`, `@NoArgsConstructor` y `@AllArgsConstructor`. Campos: `id`, `nombre`, `descripcion`, `lote`, `precioUnitario` (`BigDecimal`) y `fechaCaducidad` (`LocalDate`).

### Paso 5: crear el repositorio

`repo/ProductoRepository.java` extiende `JpaRepository<Producto, Long>`, lo que da las operaciones de consulta, guardado, actualización y borrado sin escribir SQL.

### Paso 6: crear el controlador REST

`producto/ProductoController.java` expone `GET /productos` (lista todos) y `POST /productos` (registra uno).

### Paso 7: probar la API

Con la aplicación en ejecución, en otra terminal:

```bash
curl -X POST http://localhost:8080/productos \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Creatina","descripcion":"Creatina de 500 g","lote":"12345","precioUnitario":500.50,"fechaCaducidad":"2027-05-04"}'
```

Respuesta esperada (el `id` lo asigna la base de datos):

```json
{"id":1,"nombre":"Creatina","descripcion":"Creatina de 500 g","lote":"12345","precioUnitario":500.50,"fechaCaducidad":"2027-05-04"}
```

Para consultar los productos guardados:

```bash
curl http://localhost:8080/productos
```

También abre `http://localhost:8080/productos` en el navegador. Para ver la tabla desde VS Code, usa una extensión de PostgreSQL con host `localhost`, puerto `5432`, base de datos `chochos` y usuario y contraseña `chochos`.

### Paso 8: adaptar el ejemplo a otra tabla

Se repitieron los pasos 4 a 7 con una entidad `Libro` (`id`, `titulo`, `autor`, `isbn`, `anioPublicacion`): `model/Libro.java`, `repo/LibroRepository.java` y `libro/LibroController.java`, con los endpoints `GET /libros` y `POST /libros`.

```bash
curl -X POST http://localhost:8080/libros \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Clean Code","autor":"Robert C. Martin","isbn":"9780132350884","anioPublicacion":2008}'

curl http://localhost:8080/libros
```

Hibernate crea las tablas `producto` y `libro` automáticamente al arrancar.

## Información útil

- Si la aplicación sigue ejecutándose y necesitas liberar el puerto `8080`:

  ```bash
  fuser 8080/tcp
  kill <PID>
  ```

- Si `./mvnw` marca `Permission denied`, ejecuta `chmod +x mvnw` dentro de `demo`, o usa `sh mvnw spring-boot:run`.
- Si la aplicación no conecta a la base de datos, comprueba que ejecutaste el Paso 1 y que el contenedor `db` está activo. PostgreSQL se inicia junto con el Dev Container.
- Si `psql` no se encuentra, instala el cliente con `sudo apt-get update && sudo apt-get install -y postgresql-client`.
