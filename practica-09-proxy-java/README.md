# Ejercicio: Patrón Proxy en Java

Práctica de Implementación de Sistemas sobre el patrón de diseño Proxy. Se implementa carga perezosa (lazy loading) para retrasar la carga de una imagen costosa hasta que realmente se necesita.

## Contexto del problema

El servicio `Image` carga una imagen desde disco, una operación costosa. Se quiere posponer esa carga hasta que sea necesaria y, además, controlar el acceso al objeto real.

## Estructura

```
practica-09-proxy-java/
├── Image.java          # Interfaz común
├── RealImage.java      # Objeto real: carga la imagen en su constructor
├── ProxyImage.java     # Proxy con carga perezosa
├── Main.java           # Cliente que usa el Proxy
├── MainSinProxy.java   # Versión original, sin Proxy, para comparar
└── README.md
```

## Requisitos

- JDK 8 o superior (`java --version` y `javac --version`)

## Compilar y ejecutar

Desde la raíz de la carpeta:

```bash
javac *.java
java Main
java MainSinProxy
```

## Solución

1. **`ProxyImage`** implementa `Image` y guarda el nombre del archivo y una referencia a `RealImage` que empieza en `null`.
2. **Carga perezosa:** en `display()`, si `realImage` es `null`, crea el `RealImage` (aquí ocurre la carga) y luego delega en él. Las siguientes llamadas reutilizan el objeto ya creado.
3. **Cliente:** `Main` crea un `ProxyImage` en lugar de un `RealImage`, y solo trabaja con la interfaz `Image`.

## Resultado

Con Proxy (`java Main`):

```
Application started.
Proxy created, but RealImage not loaded yet.
Loading image... photo.jpg
Displaying photo.jpg
Second call:
Displaying photo.jpg
```

Sin Proxy (`java MainSinProxy`):

```
Application started.
Loading image... photo.jpg
RealImage created (already loaded).
Displaying photo.jpg
Second call:
Displaying photo.jpg
```

## Pruebas

| Comprobación | Evidencia |
|---|---|
| Sin Proxy, `Loading image...` aparece al crear el objeto | En `MainSinProxy` aparece antes de `RealImage created` |
| Con Proxy, `Loading image...` aparece solo al llamar a `display()` | En `Main` aparece después de `Proxy created, but RealImage not loaded yet.` |
| La carga ocurre una sola vez con varias llamadas | La segunda llamada solo imprime `Displaying photo.jpg` |

## Preguntas de análisis

**Carga perezosa: ¿cuándo se ejecuta el constructor de `RealImage` en el código original y con Proxy?**
En el código original se ejecuta en el momento de crear el objeto (`new RealImage(...)`), así que la imagen se carga aunque nunca se muestre. Con Proxy se ejecuta dentro de la primera llamada a `display()`; antes de eso solo existe el `ProxyImage`, que guarda el nombre del archivo.

**Transparencia: ¿el cliente necesita saber si trabaja con un Proxy o con el objeto real?**
No. El cliente solo conoce la interfaz `Image`, y tanto `ProxyImage` como `RealImage` la implementan, así que puede usar cualquiera sin cambiar su código.

**Rendimiento: ¿qué ventajas ofrece el patrón en este escenario?**
Evita el costo de leer la imagen del disco si nunca se muestra, acelera el arranque de la aplicación y ahorra memoria. Además, como el `RealImage` se guarda después de crearlo, la carga se hace una sola vez. El costo es una pequeña indirección y que la primera llamada a `display()` es más lenta.

**Control de acceso: además de la carga perezosa, ¿qué otros controles podría implementar el Proxy?**
Verificación de permisos antes de mostrar la imagen (proxy de protección), registro de accesos (logging), caché de imágenes ya cargadas, acceso a imágenes en un servidor remoto (proxy remoto) y límites de uso, como un máximo de consultas por usuario.

**Principio SOLID: ¿cuál se aplica al hacer que `ProxyImage` y `RealImage` implementen la misma interfaz?**
El principio de sustitución de Liskov: el `ProxyImage` puede reemplazar al `RealImage` sin alterar el comportamiento esperado por el cliente. También se aplican la inversión de dependencias (el cliente depende de la abstracción `Image`) y el abierto/cerrado (se agregan proxies nuevos sin modificar `RealImage` ni el cliente).

## Escenarios de extensión

- **Proxy de protección:** `display()` consultaría los permisos del usuario antes de crear o mostrar el `RealImage`, y negaría el acceso si no los tiene.
- **Proxy de caché:** guardaría los `RealImage` ya cargados en un `Map<String, RealImage>` por nombre de archivo, para que varios proxies compartan la misma imagen.
- **Proxy de logging:** registraría fecha, usuario y archivo en cada llamada a `display()` antes de delegar.
- **Proxy remoto:** `ProxyImage` obtendría los datos desde un servidor mediante una petición de red, ocultando al cliente esa comunicación.

## Lista de comprobación

- [x] `ProxyImage` implementa `Image` y carga `RealImage` solo cuando hace falta
- [x] `Main` usa el Proxy en lugar del objeto real
- [x] La carga ocurre una sola vez con varias llamadas a `display()`
- [x] El programa compila y ejecuta correctamente
- [x] Las preguntas de análisis están respondidas
