# VitaRing 💜

**Anillo inteligente — "Tu salud y actividad física en un solo lugar".**
Prototipo universitario: app Android (celular) + app Wear OS (Galaxy Watch) que mide el pulso y lo muestra con un semáforo.

| Módulo | Qué hace |
|---|---|
| `app/` | App del celular: inicio, ritmo cardíaco, actividad (podómetro real), bienestar (estrés, SpO₂, sueño), mi anillo y resultado con semáforo. |
| `wear/` | App del reloj: lee el sensor de pulso del Galaxy Watch y lo envía en vivo al celular. |

## Semáforo del pulso

| Luz | Significado | Rango |
|---|---|---|
| 🟢 Verde | Tu pulso está en un rango normal | 60–100 lpm |
| 🟡 Amarillo | Tu pulso está en un rango moderado | 101–140 lpm |
| 🔴 Rojo | Tu pulso está en un rango alto | más de 140 lpm |

Los rangos se cambian en `app/.../data/NivelPulso.kt` (y en `wear/.../MainActivity.kt`).

## Qué es real y qué es demostración

- **Real:** pulso en vivo desde el Galaxy Watch (Wear OS) y pasos del podómetro del celular.
- **Demostración:** SpO₂, estrés, sueño, temperatura y batería del anillo (hasta tener el anillo físico).
- **Modo demo** (pantalla *Mi anillo*): simula el pulso cuando el reloj no está enviando datos. En *Resultado* puedes forzar Verde / Amarillo / Rojo para la presentación.

## Descargar e instalar

Cada vez que se sube código, GitHub compila las dos apps solo. Descárgalas en **Releases** (lado derecho del repositorio):

- `VitaRing-celular.apk` → instálalo en el celular (permite "instalar apps desconocidas").
- `VitaRing-reloj.apk` → instálalo en el Galaxy Watch (pasos abajo).

> Instala siempre los dos APK de la **misma versión**: así quedan firmados igual y pueden comunicarse.

### Instalar en el Galaxy Watch (Watch4 o más nuevo)

1. En el reloj: *Ajustes → Acerca del reloj → Software* y toca **Versión de software** 5 veces (activa el modo desarrollador).
2. *Ajustes → Opciones de desarrollador*: activa **Depuración ADB** y **Depuración inalámbrica**. Reloj y PC en la misma red Wi-Fi.
3. En *Depuración inalámbrica* toca **Emparejar nuevo dispositivo**: verás una IP:puerto y un código.
4. En la PC (con [platform-tools](https://developer.android.com/tools/releases/platform-tools)):
   ```
   adb pair IP:PUERTO_DE_EMPAREJAMIENTO   (escribe el código)
   adb connect IP:PUERTO_QUE_MUESTRA_DEPURACION_INALAMBRICA
   adb install VitaRing-reloj.apk
   ```

### Usarlo en la demo

1. Abre **VitaRing** en el celular y déjalo abierto.
2. Abre **VitaRing** en el reloj → *Permitir* → **Medir**.
3. En el celular verás "Galaxy Watch conectado · en vivo" y el pulso real con su color del semáforo.

## Abrir el código

Android Studio → *Open* → esta carpeta. Kotlin + Jetpack Compose. Colores oficiales en `app/.../ui/theme/Theme.kt`: morado `#4B2E83`, rosa `#F7D5EB`, lila `#B89BD9`.

*Mediciones referenciales. VitaRing no es un dispositivo médico.*
