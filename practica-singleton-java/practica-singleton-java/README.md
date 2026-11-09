# Ejercicio: Singleton en Java

Práctica de Implementación de Sistemas sobre el patrón de diseño Singleton. Se refactoriza la clase `AppConfig` (configuración global) para que exista una única instancia compartida por toda la aplicación.

## Objetivo

- Identificar cuándo un objeto debe mantenerse único.
- Refactorizar una clase para convertirla en Singleton con inicialización ansiosa.
- Actualizar el código cliente para usar la instancia compartida.
- Compilar y ejecutar el programa para comprobar el resultado.

## Estructura

```
practica-singleton-java/
├── AppConfig.java   # Singleton con inicialización ansiosa
├── Main.java        # Cliente que usa AppConfig.getInstance()
└── README.md
```

## Requisitos

- JDK 8 o superior (`java --version` y `javac --version`)

## Compilar y ejecutar

Desde la raíz de la carpeta:

```bash
javac AppConfig.java Main.java
java Main
```

## 1. El problema

En el código inicial, `Main` creaba dos objetos distintos con `new AppConfig()`. Cada uno tenía su propio estado, así que cambiar el tema en uno no afectaba al otro.

Salida del código inicial:

```
Theme: Dark, Language: EN
Theme: Light, Language: EN
Are these the same instance? false
```

`config1` quedó en `Dark` y `config2` siguió en `Light`: dos partes del sistema con configuraciones distintas.

## 2. Solución: Singleton con inicialización ansiosa

Cambios en `AppConfig`:

- El constructor ahora es `private`, por lo que `new AppConfig()` ya no compila fuera de la clase.
- Se agregó el campo `private static final AppConfig instance = new AppConfig();`.
- Se agregó el método `public static AppConfig getInstance()`, que devuelve esa única instancia.

```java
private static final AppConfig instance = new AppConfig();

private AppConfig() {
    this.theme = "Light";
    this.language = "EN";
}

public static AppConfig getInstance() {
    return instance;
}
```

## 3. Cliente actualizado

En `Main.java`, las llamadas a `new AppConfig()` se reemplazaron por `AppConfig.getInstance()`.

## 4. Resultado final

```
Theme: Dark, Language: EN
Theme: Dark, Language: EN
Are these the same instance? true
```

Ahora ambas referencias apuntan al mismo objeto: el cambio de tema hecho desde `config1` se ve también desde `config2`.

Comprobación adicional: al intentar `new AppConfig()` desde otra clase, el compilador marca `AppConfig() has private access in AppConfig`.

## 5. Reflexión final

**¿Cuál es el resultado de `(config1 == config2)` después de aplicar Singleton?**
`true`. Las dos variables guardan la referencia al mismo objeto, porque `getInstance()` siempre devuelve `instance`.

**¿Por qué el campo `instance` debe ser `static`?**
Porque `getInstance()` es estático y se llama sin tener un objeto todavía. Un campo `static` pertenece a la clase, no a un objeto, así que existe una sola copia aunque nunca se cree otro objeto; un campo de instancia no podría guardar "la única instancia" antes de que exista.

**¿Qué ventaja tiene garantizar una única instancia de configuración?**
Toda la aplicación trabaja con los mismos valores. Un cambio hecho en un lugar se refleja en todos, se evitan inconsistencias y se controla en un solo punto cómo se crea y se accede a la configuración.

**¿Cuál es la principal desventaja de la inicialización ansiosa?**
La instancia se crea cuando se carga la clase, aunque nunca se use. Si crearla fuera costoso (leer archivos, abrir conexiones), se gastarían recursos y tiempo de arranque sin necesidad. Además, no permite manejar con calma errores durante la creación.

**¿Qué problema puede aparecer si el Singleton guarda estado global y la aplicación crece mucho?**
Se vuelve un estado global oculto: cualquier clase puede modificarlo desde cualquier lugar, lo que dificulta rastrear quién cambió qué y provoca errores difíciles de reproducir. También crea acoplamiento entre clases, complica las pruebas unitarias (el estado persiste entre pruebas) y, con varios hilos, puede generar condiciones de carrera si el estado es modificable y no está sincronizado.

## Lista de comprobación

- [x] El constructor de `AppConfig` es privado
- [x] Existe un campo `instance` `static` y `final`
- [x] `getInstance()` devuelve la única instancia disponible
- [x] `Main` usa `AppConfig.getInstance()`
- [x] El programa compila sin errores
- [x] `config1 == config2` es `true`
- [x] Las preguntas de reflexión están respondidas
