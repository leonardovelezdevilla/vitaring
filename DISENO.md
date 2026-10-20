# Guía de diseño VitaRing

Toda pantalla nueva debe verse igual que las existentes. Toma como modelo `PantallaAnillo.kt` y `PantallaInicio.kt`.

## Colores (`ui/theme/Theme.kt`)
- **Morado** `#4B2E83`: botones principales, títulos de marca, tarjeta destacada (fondo morado + texto blanco/rosa).
- **Rosa** `#F7D5EB`: fondos de tarjetas especiales (p. ej. "Respira 1 minuto").
- **Lila** `#B89BD9`: bordes de botones secundarios, barras de gráficos, selección.
- Derivados: `Fondo` (fondo de pantalla), `LilaSuave` (selección/fondos suaves), `RosaSuave`, `RosaAcento` (corazón, progreso), `Borde`, `TextoSuave` (textos secundarios), `MoradoTexto` (texto principal).
- Semáforo: usar siempre `NivelPulso` (Verde / Amarillo / Rojo), nunca colores sueltos.

## Tipografía
- **Fredoka** (redondeada, como el logo): títulos y números grandes → `MaterialTheme.typography.headlineSmall/titleLarge` y `estiloNumero(tamaño)`.
- **Nunito**: textos de la interfaz → `titleMedium`, `bodyMedium`, `labelMedium`, `labelLarge`.
- **Patrick Hand**: solo el lema "Tu salud y actividad física en un solo lugar".

## Componentes (`ui/Componentes.kt`) — reutilizar, no inventar otros
- `Tarjeta(...)`: tarjeta blanca, esquinas 22 dp, borde fino. Variante destacada: `fondo = Morado, conBorde = false`.
- `Encabezado(titulo, subtitulo, onVolver)`: título de pantalla (con botón volver redondo).
- `Pildora(texto, fondo, tinta)`: etiquetas de estado.
- `IconoCirculo(icono, fondo, tinta)`: íconos en círculo (alternar `LilaSuave+Morado` y `RosaSuave+RosaTinta`).
- `ProgresoCircular`, `MedidorSemicircular`, `OndaPulso`: gráficos.

## Reglas
- Pantalla = `Column` con `verticalScroll`, `padding(20.dp)`, `spacedBy(14–16.dp)`.
- Botón principal: `Button` morado, alto 56 dp. Secundario: `OutlinedButton` con borde lila de 2 dp, alto 52 dp.
- Selección (p. ej. nivel de actividad): fondo `LilaSuave` + borde lila de 2 dp, como `FilaSemaforo` en `PantallaRitmo.kt`.
- Campos de texto: `OutlinedTextField` con fondo blanco, borde `Borde` y borde morado al enfocar.
- Íconos de `Icons.Rounded` (Material). Sin emojis.
- Textos en español, tono cercano ("Cuéntanos sobre ti").
