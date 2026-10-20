package pe.vitaring.app.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

/** Datos que pueden cambiar fácilmente para la presentación. */
object Config {
    const val NOMBRE_USUARIO = "Odet"
    const val META_PASOS = 10_000

    /** Ruta del mensaje que envía el reloj. Debe ser igual en el módulo wear. */
    const val RUTA_PULSO = "/vitaring/pulso"
}

/**
 * Fuente única de datos de la app.
 *
 * - Ritmo cardíaco: viene del Galaxy Watch (Wear OS) si está enviando datos;
 *   si no, y el modo demo está activo, se simula alrededor del escenario elegido.
 * - Pasos: podómetro real del celular (más un valor base en modo demo).
 * - Los demás sensores (SpO₂, estrés, sueño, temperatura) son valores de demostración
 *   hasta que el anillo físico esté listo.
 */
object HealthRepository {

    // ---- Valores de demostración ----
    const val PASOS_DEMO = 6_842
    const val SPO2_DEMO = 98
    const val ESTRES_DEMO = 32
    const val VFC_DEMO = 48
    const val RESPIRACION_DEMO = 14
    const val TEMPERATURA_DEMO = "36.5"
    const val BATERIA_DEMO = 82

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _bpm = MutableStateFlow(NivelPulso.VERDE.bpmDemo)
    val bpm: StateFlow<Int> = _bpm.asStateFlow()

    /** Últimas lecturas (una por segundo aprox.), para estadísticas y gráficos. */
    private val _lecturas = MutableStateFlow<List<Int>>(emptyList())
    val lecturas: StateFlow<List<Int>> = _lecturas.asStateFlow()

    private val _modoDemo = MutableStateFlow(true)
    val modoDemo: StateFlow<Boolean> = _modoDemo.asStateFlow()

    private val _escenarioDemo = MutableStateFlow(NivelPulso.VERDE)
    val escenarioDemo: StateFlow<NivelPulso> = _escenarioDemo.asStateFlow()

    private val _relojEnVivo = MutableStateFlow(false)
    val relojEnVivo: StateFlow<Boolean> = _relojEnVivo.asStateFlow()

    private val _pasosReales = MutableStateFlow(0)
    private val _podometroReal = MutableStateFlow(false)
    val podometroReal: StateFlow<Boolean> = _podometroReal.asStateFlow()

    /** Pasos que se muestran: reales + base de demo cuando el modo demo está activo. */
    val pasos: StateFlow<Int> = combine(_pasosReales, _modoDemo) { reales, demo ->
        if (demo) PASOS_DEMO + reales else reales
    }.stateIn(scope, SharingStarted.Eagerly, PASOS_DEMO)

    @Volatile
    private var ultimoDatoReloj = 0L

    init {
        scope.launch {
            while (isActive) {
                val enVivo = System.currentTimeMillis() - ultimoDatoReloj < 5_000
                _relojEnVivo.value = enVivo
                if (!enVivo && _modoDemo.value) {
                    val base = _escenarioDemo.value.bpmDemo
                    publicar(base + Random.nextInt(-3, 4))
                }
                delay(1_000)
            }
        }
    }

    /** Lo llama MainActivity cuando llega un mensaje del reloj. */
    fun alRecibirDelReloj(bpm: Int) {
        if (bpm !in 30..230) return
        ultimoDatoReloj = System.currentTimeMillis()
        _relojEnVivo.value = true
        publicar(bpm)
    }

    fun cambiarModoDemo(activo: Boolean) {
        _modoDemo.value = activo
    }

    /** Solo para la presentación: fuerza un color del semáforo en modo demo. */
    fun simular(nivel: NivelPulso) {
        _escenarioDemo.value = nivel
        publicar(nivel.bpmDemo)
    }

    fun actualizarPasosReales(pasos: Int) {
        _pasosReales.value = pasos.coerceAtLeast(0)
    }

    fun marcarPodometro(funciona: Boolean) {
        _podometroReal.value = funciona
    }

    private fun publicar(valor: Int) {
        _bpm.value = valor
        _lecturas.update { (it + valor).takeLast(300) }
    }
}

/** Mínimo, promedio y máximo de una lista de lecturas. */
data class Estadisticas(val minimo: Int, val promedio: Int, val maximo: Int)

fun estadisticas(lecturas: List<Int>): Estadisticas =
    if (lecturas.isEmpty()) Estadisticas(0, 0, 0)
    else Estadisticas(lecturas.min(), lecturas.average().toInt(), lecturas.max())
