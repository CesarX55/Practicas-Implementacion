# Patrón State: sistema de gestión de estados de pedidos

Práctica de Implementación de Sistemas sobre el patrón de diseño State. Se refactoriza la clase `Pedido`, que manejaba sus estados con condicionales y banderas, para delegar el comportamiento en clases de estado.

## Contexto

La clase `Pedido` original controlaba todos los estados con condicionales y banderas booleanas, lo que dificultaba su mantenimiento y ampliación. Con State, cada estado es una clase con su propio comportamiento, y el pedido solo delega en el estado actual.

## Estructura

```
practica-10-state-java/
├── EstadoPedido.java       # Interfaz con las cuatro acciones
├── EstadoPendiente.java    # Estados concretos
├── EstadoProcesado.java
├── EstadoEnviado.java
├── EstadoEntregado.java
├── EstadoCancelado.java
├── Pedido.java             # Contexto: guarda el estado actual y delega
├── Main.java               # Demostración de las transiciones
└── README.md
```

## Requisitos

- JDK 8 o superior (`java --version` y `javac --version`)

## Compilar y ejecutar

```bash
javac -encoding UTF-8 *.java
java Main
```

Si en PowerShell los acentos se ven mal, ejecuta antes `chcp 65001`.

## Solución

1. Se crea la interfaz `EstadoPedido` con `procesar`, `cancelar`, `enviar` y `entregar`, cada una recibe el `Pedido`.
2. Se crean cinco estados concretos. Cada uno decide qué acciones son válidas (y a qué estado pasa el pedido) y cuáles imprimen un error.
3. `Pedido` elimina las banderas booleanas, guarda un `EstadoPedido estadoActual` (inicialmente `EstadoPendiente`) y delega cada operación en él. `setEstado()` permite que los estados cambien el estado del pedido.

### Transiciones

| Estado actual | procesar | cancelar | enviar | entregar |
|---|---|---|---|---|
| Pendiente | → Procesado | → Cancelado | error | error |
| Procesado | error | error | → Enviado | error |
| Enviado | error | error | error | → Entregado |
| Entregado | error | error | error | error |
| Cancelado | error | error | error | error |

`EstadoProcesado` y `EstadoPendiente` siguen el comportamiento del enunciado. `EstadoEnviado`, `EstadoEntregado` y `EstadoCancelado` completan el flujo: un pedido enviado solo puede entregarse, y los pedidos entregados o cancelados son estados finales.

## Resultado

```
=== Flujo normal: pendiente -> procesado -> enviado -> entregado ===
Estado actual: EstadoPendiente
Procesando pedido...
Estado actual: EstadoProcesado
Enviando pedido...
Estado actual: EstadoEnviado
Entregando pedido...
Estado actual: EstadoEntregado

=== Transiciones inválidas desde pendiente ===
ERROR: No se puede enviar un pedido pendiente
ERROR: No se puede entregar un pedido pendiente
Estado actual: EstadoPendiente

=== Cancelación de un pedido pendiente ===
Cancelando pedido...
Estado actual: EstadoCancelado
ERROR: No se puede procesar un pedido cancelado
Estado actual: EstadoCancelado

=== No se puede cancelar un pedido ya procesado ===
Procesando pedido...
ERROR: No se puede cancelar un pedido ya procesado
Estado actual: EstadoProcesado

=== Un pedido entregado ya no cambia ===
ERROR: No se puede cancelar un pedido ya entregado
ERROR: El pedido ya fue entregado
Estado actual: EstadoEntregado
```

## Ventajas del patrón en este ejercicio

- Desaparecen los condicionales anidados y las banderas: cada estado contiene solo su comportamiento.
- Agregar un estado nuevo (por ejemplo, "Devuelto") implica crear una clase y ajustar las transiciones que llegan a ella, sin reescribir una clase `Pedido` llena de `if`.
- Las transiciones inválidas quedan explícitas en cada estado, en lugar de estar dispersas en un solo método.

## Lista de comprobación

- [x] Los flags booleanos fueron reemplazados por un atributo `EstadoPedido estadoActual`
- [x] `EstadoPedido` y los cinco estados concretos están implementados
- [x] `Pedido` delega todas sus operaciones al estado actual
- [x] El programa compila y ejecuta correctamente
