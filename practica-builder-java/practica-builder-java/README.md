# Ejercicio: Patrón Builder en Java

Práctica de Implementación de Sistemas sobre el patrón de diseño Builder. Se construyen objetos `User` con parámetros obligatorios y opcionales sin recurrir a constructores telescópicos ni a setters.

## Objetivo

- Crear una clase `Builder` estática interna dentro de `User`.
- Separar los parámetros obligatorios (en el constructor del `Builder`) de los opcionales (métodos encadenables).
- Construir el objeto final con `build()` y un constructor privado.
- Usar el `Builder` desde el código cliente.

## Estructura

```
practica-builder-java/
├── User.java    # Clase inmutable con Builder estático interno
├── Main.java    # Cliente que crea usuarios con distintas combinaciones
└── README.md
```

## Requisitos

- JDK 8 o superior (`java --version` y `javac --version`)

## Compilar y ejecutar

Desde la raíz de la carpeta:

```bash
javac -encoding UTF-8 User.java Main.java
java Main
```

Si en PowerShell los acentos se ven mal (`PÃ©rez`), ejecuta antes `chcp 65001` o usa `java -Dfile.encoding=UTF-8 Main`.

## El problema

Un `User` tiene dos datos obligatorios (`firstName`, `lastName`) y tres opcionales (`age`, `phone`, `address`). Con constructores telescópicos habría que escribir una versión por cada combinación y recordar el orden de los argumentos; con setters, el objeto existiría incompleto y modificable durante su construcción.

## La solución

1. **`Builder` estático interno** con los mismos campos que `User`: los obligatorios son `final` y los opcionales tienen valor por defecto (`age = 0`, `phone = ""`, `address = ""`).
2. **Constructor del `Builder`** que recibe solo `firstName` y `lastName`.
3. **Métodos `age()`, `phone()` y `address()`** que asignan el valor y devuelven `this` para encadenar llamadas.
4. **`build()`** que devuelve `new User(this)`.
5. **Constructor privado de `User`** que copia los valores del `Builder`. Todos los campos de `User` son `private final` y no existen setters.
6. **`Main`** crea los usuarios con `new User.Builder(...)....build()`.

## Resultado

```
User{firstName='Juan', lastName='Pérez', age=30, phone='+123456789', address='Calle Falsa 123'}
User{firstName='María', lastName='Gómez', age=0, phone='+987654321', address=''}
User{firstName='Carlos', lastName='López', age=25, phone='', address='Avenida Siempre Viva 742'}
```

Se crearon tres usuarios con combinaciones distintas de parámetros. Los datos no indicados conservan su valor por defecto (`age=0`, cadenas vacías).

## Pruebas de inmutabilidad

Se intentó modificar o crear un `User` fuera del `Builder`, y el compilador lo rechaza:

| Intento | Resultado |
|---|---|
| `user.age = 5;` | `error: age has private access in User` |
| `user.setAge(5);` | `error: cannot find symbol` (no existen setters) |
| `new User(...)` desde otra clase | `error: User(Builder) has private access in User` |

## Preguntas de análisis

**¿Cómo mejora la legibilidad del código cliente el uso del patrón Builder?**
Cada valor se indica con el nombre del dato (`.age(30)`, `.phone("+123456789")`), así que no hay que recordar el orden de los parámetros ni distinguir entre argumentos del mismo tipo, como tres `String` seguidos. Solo se escribe lo que se necesita, sin pasar valores vacíos para llenar un constructor largo, y el código se lee casi como una frase. Además se evita la explosión de constructores para cada combinación de opcionales.

**¿Qué ventaja tiene hacer la clase `Builder` estática e interna?**
Al ser estática, no necesita una instancia de `User` para existir: se crea con `new User.Builder(...)`. Si no fuera estática, haría falta un `User` para crear el `Builder` que sirve para crear un `User`, lo cual es circular. Al ser interna, el `Builder` y `User` pueden acceder a los miembros privados del otro, por lo que `User` mantiene su constructor privado y nadie más puede construirlo. También deja claro, por su nombre y ubicación, que el `Builder` pertenece a `User`.

**¿Garantiza este patrón la inmutabilidad del objeto `User`? ¿Por qué?**
No por sí solo. El patrón Builder organiza la construcción, pero no impide que el objeto resultante sea modificable: si `User` tuviera setters o campos no `final`, seguiría siendo mutable. Aquí la inmutabilidad se logra porque los campos son `private final`, no hay setters y el constructor es privado. Además, `String` e `int` son inmutables o primitivos; si `User` guardara un objeto mutable (una lista o una fecha), habría que hacer copias defensivas. Por último, el `Builder` sí es mutable, pero eso no afecta a los `User` ya creados porque `build()` copia los valores.

## Lista de comprobación

- [x] `Builder` es una clase estática interna de `User`
- [x] El constructor del `Builder` recibe solo los parámetros obligatorios
- [x] Los parámetros opcionales se establecen con métodos que devuelven `this`
- [x] `build()` devuelve una nueva instancia de `User`
- [x] El constructor de `User` es privado y recibe un `Builder`
- [x] `Main` crea usuarios con `User.Builder`
- [x] El programa compila y ejecuta correctamente
- [x] Las preguntas de análisis están respondidas
