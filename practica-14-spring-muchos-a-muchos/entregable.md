# Entregable: relación muchos a muchos (Producto y Department)

**Paso 1: ¿Cómo afectará JPA a la base de datos? ¿Qué tabla creará y qué columnas tendrá?**
Además de las tablas de las entidades (`categorias` para `Department` y `producto` para `Producto`), JPA creará la tabla de unión `producto_deparments`, definida con `@JoinTable`. Tendrá dos columnas, `product_id` y `deparments_id`, que forman la clave primaria compuesta (al ser un `Set`, no se repite un par). Como `Department` es el lado propietario, `joinColumns` (`product_id`) referencia al propietario, es decir, a `categorias.id`, y `inverseJoinColumns` (`deparments_id`) referencia a `producto.id`. Los nombres de las columnas quedaron intercambiados respecto a lo que contienen; el esquema funciona, pero conviene tenerlo en cuenta al leer la tabla. Esto es lo esperado según las anotaciones; se confirma en PostgreSQL con `\d producto_deparments`.

**Paso 2: ¿Qué significa `@JsonIgnore` y por qué se usa en `Producto`?**
`@JsonIgnore` hace que Jackson omita la propiedad al convertir el objeto a JSON (y al leerlo). En `Producto.deparments` se usa para romper el ciclo `Producto → Department → Producto`: sin ella, al serializar un producto se incluirían sus categorías, cada categoría volvería a incluir sus productos, y así sin fin (desbordamiento de pila). También evita devolver las categorías dentro de cada producto; para consultarlas existe `GET /categoria/producto/{productId}`.

**Paso 3: ¿Cuál es la función de `addProductoCategoria/{productId}/{categoriaId}`?**
Asocia un producto existente con una categoría existente. Busca ambos por su ID, agrega el producto al conjunto `productos` de la categoría y guarda la categoría; como `Department` es el lado propietario, ese guardado inserta una fila en la tabla `producto_deparments`. Hay que tener en cuenta que, tal como está escrito, si la categoría no existe el método falla con `NoSuchElementException` (por `d.get()`), y si el producto no existe devuelve la categoría sin cambios.

**Paso 3: ¿Por qué se declaran dos repositorios, `categoriaRepo` y `productoRepo`?**
Porque cada `JpaRepository` administra una sola entidad. `categoriaRepo` accede a la tabla de categorías y `productoRepo` a la de productos; el endpoint necesita leer ambas para poder relacionarlas.
