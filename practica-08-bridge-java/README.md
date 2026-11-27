# Ejercicio: Patrón Bridge en Java

Práctica de Implementación de Sistemas sobre el patrón de diseño Bridge. Se desacoplan los controles remotos (abstracción) de los dispositivos (implementación) para que ambas jerarquías evolucionen de forma independiente.

## Contexto del problema

Hay distintos dispositivos (`Radio`, `TV`) y distintos controles remotos (`ControlRemotoBasico`, `ControlRemotoAvanzado`). Sin el patrón habría que crear una clase por cada combinación (`ControlRemotoBasicoRadio`, `ControlRemotoAvanzadoTV`, etc.).

## Estructura

```
practica-08-bridge-java/
├── Dispositivo.java              # Implementación (interfaz)
├── Radio.java                    # Implementaciones concretas
├── TV.java
├── ControlRemoto.java            # Abstracción con el "puente" hacia Dispositivo
├── ControlRemotoBasico.java      # Abstracciones refinadas
├── ControlRemotoAvanzado.java
├── Main.java                     # Las cuatro combinaciones del ejercicio
├── Projector.java                # Extensión: nuevo dispositivo
├── ControlRemotoVoz.java         # Extensión: nuevo control
├── MainExtension.java            # Demostración de las extensiones
└── README.md
```

## Requisitos

- JDK 8 o superior (`java --version` y `javac --version`)

## Compilar y ejecutar

Desde la raíz de la carpeta:

```bash
javac -encoding UTF-8 *.java
java Main
java MainExtension
```

Si en PowerShell los acentos se ven mal (`BÃ¡sico`), ejecuta antes `chcp 65001`.

## Las dos jerarquías

| Jerarquía | Tipos | Papel |
|---|---|---|
| Implementación | `Dispositivo`, `Radio`, `TV` | Hacen el trabajo real: encender, volumen y canal |
| Abstracción | `ControlRemoto`, `ControlRemotoBasico`, `ControlRemotoAvanzado` | Ofrecen los botones y delegan en un `Dispositivo` |

El puente es la composición `protected Dispositivo dispositivo;` de `ControlRemoto`: cada control recibe un dispositivo en su constructor y le delega las operaciones, sin conocer si es una radio o una TV.

`ControlRemotoAvanzado` agrega `mute()` y `record()`, que no existen en el control básico.

## Resultado de `Main`

```
=== Control Básico con Radio ===
Control Básico: Botón power presionado
Radio encendida
Control Básico: Subir volumen
Radio: volumen en 40%
Control Básico: Canal siguiente
Radio: sintonizando canal 2
Control Básico: Bajar volumen
Radio: volumen en 30%
Control Básico: Botón power presionado
Radio apagada

=== Control Avanzado con TV ===
Control Avanzado: Power inteligente
TV encendida
Control Avanzado: Subir volumen
TV: volumen en 40%
Control Avanzado: Canal siguiente
TV: cambiando al canal 2
Control Avanzado: Bajar volumen
TV: volumen en 30%
Control Avanzado: Power inteligente
TV apagada
Control Avanzado: Silenciar
TV: volumen en 0%

=== Control Básico con TV ===
Control Básico: Botón power presionado
TV encendida
Control Básico: Subir volumen
TV: volumen en 40%
Control Básico: Canal siguiente
TV: cambiando al canal 2
Control Básico: Bajar volumen
TV: volumen en 30%
Control Básico: Botón power presionado
TV apagada

=== Control Avanzado con Radio ===
Control Avanzado: Power inteligente
Radio encendida
Control Avanzado: Subir volumen
Radio: volumen en 40%
Control Avanzado: Canal siguiente
Radio: sintonizando canal 2
Control Avanzado: Bajar volumen
Radio: volumen en 30%
Control Avanzado: Power inteligente
Radio apagada
Control Avanzado: Grabando programa actual
Radio -> apagada, volumen: 30%, canal: 2
```

Con 2 controles y 2 dispositivos se obtienen 4 combinaciones usando solo 4 clases concretas. Observa que `record()` se llama después de apagar el dispositivo porque así está escrito `Main` en el enunciado; por eso el estado impreso es `apagada`.

## Ejercicios de extensión

**Nuevo dispositivo: `Projector`.** Solo fue necesario crear una clase que implementa `Dispositivo` (el "canal" es la entrada HDMI, de 1 a 4). Ningún control ni `Main` cambió.

**Nuevo control: `ControlRemotoVoz`.** Extiende `ControlRemoto` y agrega `escuchar(String comando)`, que interpreta frases como "encender", "subir volumen" o "silenciar". Funciona con cualquier dispositivo existente.

**Cambio de dispositivo en tiempo de ejecución.** Se agregó `setDispositivo(...)` a `ControlRemoto` para reasignar el puente.

Resultado de `MainExtension`:

```
=== Control Básico con Projector ===
Control Básico: Botón power presionado
Proyector encendido
Control Básico: Subir volumen
Proyector: volumen en 40%
Control Básico: Canal siguiente
Proyector: entrada HDMI 2
Control Básico: Bajar volumen
Proyector: volumen en 30%
Control Básico: Botón power presionado
Proyector apagado

=== Control Avanzado con Projector ===
Control Avanzado: Power inteligente
Proyector encendido
Control Avanzado: Subir volumen
Proyector: volumen en 40%
Control Avanzado: Canal siguiente
Proyector: entrada HDMI 2
Control Avanzado: Bajar volumen
Proyector: volumen en 30%
Control Avanzado: Power inteligente
Proyector apagado
Control Avanzado: Grabando programa actual
Proyector -> apagado, volumen: 30%, entrada HDMI: 2

=== Control de Voz con TV ===
Control de Voz: escuché "encender"
TV encendida
Control de Voz: escuché "subir volumen"
TV: volumen en 40%
Control de Voz: escuché "canal siguiente"
TV: cambiando al canal 2
Control de Voz: escuché "silenciar"
TV: volumen en 0%
Control de Voz: escuché "apagar"
TV apagada
Control de Voz: escuché "bailar"
Control de Voz: no entendí el comando

=== Cambio de dispositivo en tiempo de ejecución ===
Control Avanzado: Power inteligente
Radio encendida
Control Avanzado: Power inteligente
TV encendida
Control Avanzado: Power inteligente
Proyector encendido
```

**Funcionalidad cruzada (¿y si un control avanzado solo funcionara con ciertos dispositivos?).**
Lo manejaría con una interfaz opcional, por ejemplo `Grabable`, implementada solo por los dispositivos capaces de grabar. `ControlRemotoAvanzado.record()` comprobaría `dispositivo instanceof Grabable` y, si no lo es, informaría que la función no está soportada. Evita inflar la interfaz `Dispositivo` con métodos que no todos pueden cumplir (segregación de interfaces). Otras opciones: validar en el constructor y lanzar `IllegalArgumentException`, o usar genéricos (`ControlRemotoGrabador<T extends Dispositivo & Grabable>`) para que la combinación inválida ni siquiera compile.

## Preguntas de análisis

**Explosión de clases: ¿cuántas clases serían necesarias con 3 controles y 4 dispositivos sin Bridge? ¿Y con Bridge?**
Sin Bridge: 3 × 4 = 12 clases concretas, una por cada combinación. Con Bridge: 3 + 4 = 7 clases concretas (más la abstracción `ControlRemoto` y la interfaz `Dispositivo`, 9 tipos en total). Al agregar un dispositivo nuevo, sin Bridge se necesitarían 3 clases más; con Bridge, solo 1.

**Acoplamiento: ¿qué pasaría si `ControlRemotoBasico` heredara directamente de `Radio` o `TV`?**
Quedaría atado a un dispositivo concreto desde la compilación y se necesitaría una clase por cada combinación, que es justo la explosión de clases que el patrón evita. Además, la herencia diría que un control "es una" radio, lo cual es incorrecto, y Java solo permite heredar de una clase, así que un mismo control no podría trabajar con `Radio` y con `TV`. Cualquier cambio en el dispositivo afectaría al control y no se podría cambiar de dispositivo mientras el programa corre.

**Extensibilidad: para agregar un `Projector`, ¿qué cambios hay que hacer? ¿Hay que modificar los controles existentes?**
Solo crear la clase `Projector` que implemente `Dispositivo`. No se modifica ningún control existente: todos reciben un `Dispositivo` y no saben qué dispositivo concreto es. `MainExtension` lo demuestra con los controles básico y avanzado.

**Principios SOLID: ¿cuáles se aplican?**
- *Inversión de dependencias:* `ControlRemoto` depende de la abstracción `Dispositivo`, no de `Radio` ni de `TV`.
- *Abierto/cerrado:* se agregan dispositivos o controles nuevos extendiendo, sin modificar código existente.
- *Responsabilidad única:* los controles gestionan la interacción con el usuario y los dispositivos gestionan su propio estado.
- *Sustitución de Liskov:* cualquier control o dispositivo puede sustituir a otro de su jerarquía; `probarControlRemoto` acepta cualquier `ControlRemoto`.
- También se favorece la composición sobre la herencia.

**Flexibilidad: ¿cómo nos beneficiamos al poder cambiar el dispositivo de un control en tiempo de ejecución?**
Un mismo control puede manejar distintos dispositivos sin crear otro objeto, como muestra `MainExtension`: el control avanzado pasa de una radio a una TV y luego a un proyector. Eso permite reutilizar el control, reconfigurar el sistema según el entorno y probar el control con un dispositivo falso en lugar de uno real.

## Lista de comprobación

- [x] `Dispositivo`, `Radio` y `TV` implementados
- [x] `ControlRemoto` contiene el puente hacia `Dispositivo`
- [x] `ControlRemotoBasico` y `ControlRemotoAvanzado` implementados
- [x] `Main` prueba las cuatro combinaciones
- [x] Ejercicios de extensión: `Projector`, `ControlRemotoVoz` y funcionalidad cruzada
- [x] El programa compila y ejecuta correctamente
- [x] Las preguntas de análisis están respondidas
