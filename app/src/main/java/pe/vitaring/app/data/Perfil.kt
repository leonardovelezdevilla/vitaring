package pe.vitaring.app.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Nivel de actividad que el usuario elige al registrarse. */
enum class Actividad(
    val titulo: String,
    val descripcion: String,
    val metaPasos: Int,
    val horasRecordatorio: Int,
) {
    SEDENTARIO("Sedentario", "Paso la mayor parte del día sentado", 6_000, 1),
    REGULAR("Actividad física regular", "Camino o hago ejercicio algunas veces por semana", 8_000, 2),
    DEPORTISTA("Deportista", "Entreno casi todos los días", 12_000, 3),
}

/** Datos personales: con ellos se personalizan metas y cálculos. */
data class Perfil(
    val nombre: String,
    val edad: Int,
    val pesoKg: Float,
    val tallaCm: Int,
    val actividad: Actividad,
) {
    val imc: Float get() = pesoKg / ((tallaCm / 100f) * (tallaCm / 100f))

    val categoriaImc: String
        get() = when {
            imc < 18.5f -> "Bajo peso"
            imc < 25f -> "Normal"
            imc < 30f -> "Sobrepeso"
            else -> "Obesidad"
        }

    /** Longitud de paso estimada según la talla. */
    private val zancadaM: Float get() = tallaCm * 0.415f / 100f

    fun km(pasos: Int): Float = pasos * zancadaM / 1000f

    /** Calorías aproximadas por caminar, según el peso. */
    fun kcal(pasos: Int): Int = (pasos * pesoKg * 0.0005f).toInt()

    /** Frecuencia cardíaca máxima estimada (fórmula 220 − edad). */
    val pulsoMaximo: Int get() = 220 - edad

    /** Horas de sueño recomendadas según la edad. */
    val metaSuenoHoras: Int
        get() = when {
            edad < 18 -> 9
            edad >= 65 -> 7
            else -> 8
        }
}

/** Guarda y lee el perfil en el celular. */
object PerfilRepo {
    private const val ARCHIVO = "perfil"

    private val _perfil = MutableStateFlow<Perfil?>(null)
    val perfil: StateFlow<Perfil?> = _perfil.asStateFlow()

    fun cargar(context: Context) {
        val p = context.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)
        val nombre = p.getString("nombre", null) ?: return
        val actividad = runCatching { Actividad.valueOf(p.getString("actividad", null) ?: "") }
            .getOrDefault(Actividad.REGULAR)
        _perfil.value = Perfil(
            nombre = nombre,
            edad = p.getInt("edad", 20),
            pesoKg = p.getFloat("peso", 60f),
            tallaCm = p.getInt("talla", 165),
            actividad = actividad,
        )
    }

    fun guardar(context: Context, perfil: Perfil) {
        context.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE).edit()
            .putString("nombre", perfil.nombre)
            .putInt("edad", perfil.edad)
            .putFloat("peso", perfil.pesoKg)
            .putInt("talla", perfil.tallaCm)
            .putString("actividad", perfil.actividad.name)
            .apply()
        _perfil.value = perfil
    }
}
