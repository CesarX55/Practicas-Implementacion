# Patrón Adapter: ejercicio de programación en Java

Práctica de Implementación de Sistemas sobre el patrón de diseño Adapter. Se permite que un reproductor que solo entiende MP3 use reproductores avanzados (VLC y MP4) con interfaces incompatibles.

## Objetivo

Comprender cómo el patrón Adapter permite que interfaces incompatibles trabajen juntas mediante un puente entre ellas.

## El problema de incompatibilidad

- `AudioPlayer` solo puede reproducir archivos MP3 y trabaja con la interfaz `MediaPlayer` (`play(audioType, fileName)`).
- `VlcPlayer` y `Mp4Player` implementan `AdvancedMediaPlayer`, que expone `playVlc` y `playMp4`.
- Las dos interfaces son incompatibles: `AudioPlayer` no puede llamar directamente a los reproductores avanzados.

## Estructura

```
practica-11-adapter-java/
├── MediaPlayer.java          # Interfaz que usa el cliente
├── AdvancedMediaPlayer.java  # Interfaz incompatible (adaptee)
├── VlcPlayer.java
├── Mp4Player.java
├── MediaAdapter.java         # Adaptador
├── AudioPlayer.java          # Cliente: usa el adaptador
├── AdapterPatternDemo.java   # Prueba de todos los tipos de medio
└── README.md
```

## Requisitos

- JDK 8 o superior (`java --version` y `javac --version`)

## Compilar y ejecutar

```bash
javac -encoding UTF-8 *.java
java AdapterPatternDemo
```

## Solución paso a paso

1. **`MediaAdapter`** implementa `MediaPlayer` y guarda un `AdvancedMediaPlayer`. Su constructor recibe `audioType` e inicializa el reproductor adecuado (`VlcPlayer` o `Mp4Player`).
2. **`play` en `MediaAdapter`:** traduce la llamada `play(audioType, fileName)` al método correspondiente de `AdvancedMediaPlayer` (`playVlc` o `playMp4`).
3. **`AudioPlayer`:** conserva la reproducción de MP3 y, para `vlc` y `mp4`, crea un `MediaAdapter` y le delega la reproducción. Para otros tipos informa que el tipo es inválido.
4. **Prueba:** `AdapterPatternDemo` reproduce mp3, mp4, vlc y un formato no soportado (avi).

## Resultado

```
Reproduciendo archivo mp3: song.mp3
Reproduciendo archivo mp4: video.mp4
Reproduciendo archivo vlc: movie.vlc
Tipo de medio inválido: avi
```

## Cómo funciona el adaptador

`AudioPlayer` solo conoce `MediaPlayer`. Cuando recibe un formato que no sabe reproducir, se apoya en `MediaAdapter`, que por fuera se ve como un `MediaPlayer` y por dentro traduce la llamada a la interfaz de `AdvancedMediaPlayer`. Así se reutilizan `VlcPlayer` y `Mp4Player` sin modificarlos y sin cambiar la interfaz que usa el cliente.

## Lista de comprobación

- [x] `MediaAdapter` implementa `MediaPlayer` y contiene un `AdvancedMediaPlayer`
- [x] El constructor inicializa el reproductor según `audioType`
- [x] `play` delega en `playVlc` o `playMp4` según el tipo
- [x] `AudioPlayer` usa el adaptador y conserva MP3
- [x] Se prueban mp3, mp4, vlc y un tipo inválido
- [x] El programa compila y ejecuta correctamente
