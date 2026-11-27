# Entregable: relación uno a muchos (Product y Comment)

**1. ¿En qué tabla queda la llave foránea y qué propiedad de Java la controla?**
La llave foránea queda en la tabla del lado "muchos", `comentarios`, normalmente en la columna `producto_id`. La controla la propiedad `Comment.producto` (`@ManyToOne`), que es el lado propietario de la relación. `Product.comentarios` es el lado inverso, marcado con `mappedBy = "producto"`, que se refiere al nombre de la propiedad Java y no al de la tabla.

**2. ¿Cuál es la diferencia entre `List` y `Set`?**
Una `List` conserva el orden de inserción y permite elementos repetidos. Un `Set` no promete orden y no admite elementos repetidos según `equals` y `hashCode`. `Product` usa `Set<Comment>` para no duplicar comentarios; al usar entidades en un `Set`, conviene definir bien `equals` y `hashCode`.

**3. ¿Por qué los identificadores son `String` y qué tipo de valor guardan?**
Porque usan `@GeneratedValue(strategy = GenerationType.UUID)`: JPA genera un UUID (36 caracteres con guiones, por ejemplo `3f2b8c1e-...`) y lo guarda como texto. Un UUID es único sin depender de un contador de la base de datos, no revela cuántos registros existen y no es secuencial, a diferencia de un entero autoincremental.

**4. ¿Qué ocurre al eliminar un producto con comentarios asociados? ¿Qué cambia si se quita `CascadeType.ALL`?**
Con `CascadeType.ALL` (que incluye `REMOVE`), la eliminación del producto mediante JPA se propaga a sus comentarios: JPA borra primero los comentarios y luego el producto, sin violar la llave foránea. Si se quita `CascadeType.ALL`, JPA solo intenta borrar el producto y PostgreSQL rechaza la operación porque todavía hay comentarios que lo referencian (error de integridad referencial), a menos que se borren antes los comentarios o la base tenga `ON DELETE CASCADE`. La cascada es un comportamiento de JPA, no una regla creada en PostgreSQL.

**5. ¿Qué diferencia práctica hay entre los `FetchType` usados en `Product` y `Comment`?**
`Product.comentarios` es `EAGER`: al cargar un producto también se cargan sus comentarios, lo que es cómodo para devolverlos en el JSON pero puede traer más datos de los necesarios si hay muchos. `Comment.producto` es `LAZY`: el producto no se carga hasta que se accede a él, lo que evita consultas innecesarias, pero con `spring.jpa.open-in-view=false` acceder a la relación fuera de una transacción lanza `LazyInitializationException`. Por eso `@JsonBackReference` impide que el JSON de un comentario intente leer su producto.
