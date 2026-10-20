package pe.vitaring.app.data

import androidx.compose.ui.graphics.Color

/**
 * Semáforo del pulso (igual que el póster "Colores de la luz").
 * Rangos referenciales en reposo; ajústenlos con su profesor si lo piden.
 */
enum class NivelPulso(
    val nombre: String,
    val etiqueta: String,
    val mensaje: String,
    val rango: String,
    val consejo: String,
    val colorLuz: Color,
    val fondo: Color,
    val tinta: Color,
    val bpmDemo: Int,
) {
    VERDE(
        nombre = "Verde",
        etiqueta = "normal",
        mensaje = "Tu pulso está en un rango normal.",
        rango = "60–100 lpm",
        consejo = "Todo en orden. Puedes continuar con tu actividad.",
        colorLuz = Color(0xFF2E9E62),
        fondo = Color(0xFFDDF3E6),
        tinta = Color(0xFF145C3B),
        bpmDemo = 72,
    ),
    AMARILLO(
        nombre = "Amarillo",
        etiqueta = "moderado",
        mensaje = "Tu pulso está en un rango moderado.",
        rango = "101–140 lpm",
        consejo = "Baja el ritmo y respira profundo unos minutos.",
        colorLuz = Color(0xFFF2C230),
        fondo = Color(0xFFFDF3CF),
        tinta = Color(0xFF7A5600),
        bpmDemo = 118,
    ),
    ROJO(
        nombre = "Rojo",
        etiqueta = "alto",
        mensaje = "Tu pulso está en un rango alto.",
        rango = "Más de 140 lpm",
        consejo = "Detén el ejercicio y descansa. Si no baja, busca ayuda médica.",
        colorLuz = Color(0xFFE0605A),
        fondo = Color(0xFFFBE1DF),
        tinta = Color(0xFF9A2A24),
        bpmDemo = 152,
    );

    companion object {
        const val LIMITE_MODERADO = 100
        const val LIMITE_ALTO = 140

        fun para(bpm: Int): NivelPulso = when {
            bpm > LIMITE_ALTO -> ROJO
            bpm > LIMITE_MODERADO -> AMARILLO
            else -> VERDE
        }
    }
}
