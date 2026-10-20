package pe.vitaring.wear

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.math.roundToInt

/** Debe ser igual a Config.RUTA_PULSO en la app del celular. */
private const val RUTA_PULSO = "/vitaring/pulso"

private val Morado = Color(0xFF4B2E83)
private val Lila = Color(0xFFB89BD9)
private val RosaBrillo = Color(0xFFF4A7C9)
private val LilaTexto = Color(0xFFD9CCF0)
private val Pista = Color(0xFF2A2140)

private val Fredoka = FontFamily(Font(R.font.fredoka, FontWeight.SemiBold), Font(R.font.fredoka, FontWeight.Bold))

/** Semáforo (mismos rangos que en el celular), con colores más brillantes para fondo negro. */
private enum class Nivel(val texto: String, val color: Color) {
    VERDE("Verde · normal", Color(0xFF3FBF7A)),
    AMARILLO("Amarillo · moderado", Color(0xFFF2C230)),
    ROJO("Rojo · alto", Color(0xFFF0716B));

    companion object {
        fun para(bpm: Int) = when {
            bpm > 140 -> ROJO
            bpm > 100 -> AMARILLO
            else -> VERDE
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Mantener la pantalla encendida mientras se usa la app (útil en la demo)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent { MaterialTheme { PantallaReloj() } }
    }
}

@Composable
private fun PantallaReloj() {
    val contexto = LocalContext.current
    val sensor = remember { SensorPulso(contexto) }
    val enviador = remember { EnviadorCelular(contexto) }

    var permiso by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(contexto, Manifest.permission.BODY_SENSORS) ==
                PackageManager.PERMISSION_GRANTED,
        )
    }
    val pedirPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { permiso = it }

    var midiendo by remember { mutableStateOf(false) }
    var bpm by remember { mutableIntStateOf(0) }
    var celular by remember { mutableStateOf<Boolean?>(null) }

    // Mientras "midiendo" sea true, leemos el sensor y enviamos al celular cada segundo
    LaunchedEffect(midiendo) {
        if (!midiendo) return@LaunchedEffect
        var ultimoEnvio = 0L
        sensor.lecturas().collect { valor ->
            bpm = valor
            val ahora = System.currentTimeMillis()
            if (ahora - ultimoEnvio >= 1_000) {
                ultimoEnvio = ahora
                celular = enviador.enviar(valor)
            }
        }
    }

    val nivel = Nivel.para(bpm)

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center,
    ) {
        // Aro exterior con el color del semáforo
        Canvas(Modifier.fillMaxSize().padding(4.dp)) {
            val g = 8.dp.toPx()
            val tamano = Size(size.width - g, size.height - g)
            val origen = Offset(g / 2, g / 2)
            drawArc(Pista, 0f, 360f, false, origen, tamano, style = Stroke(g))
            if (bpm > 0) {
                val fraccion = (bpm / 180f).coerceIn(0.05f, 1f)
                drawArc(nivel.color, -90f, 360f * fraccion, false, origen, tamano, style = Stroke(g, cap = StrokeCap.Round))
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(horizontal = 24.dp),
        ) {
            Text("VITARING", fontFamily = Fredoka, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Lila, letterSpacing = 1.sp)

            when {
                !sensor.disponible -> Text(
                    "Este reloj no tiene sensor de pulso disponible",
                    fontSize = 13.sp, color = LilaTexto, textAlign = TextAlign.Center,
                )

                !permiso -> {
                    Text("Permite el acceso al sensor de pulso", fontSize = 13.sp, color = LilaTexto, textAlign = TextAlign.Center)
                    CompactChip(
                        onClick = { pedirPermiso.launch(Manifest.permission.BODY_SENSORS) },
                        label = { Text("Permitir", fontWeight = FontWeight.Bold) },
                        colors = ChipDefaults.chipColors(backgroundColor = Lila, contentColor = Morado),
                    )
                }

                else -> {
                    Text("♥", fontSize = 18.sp, color = RosaBrillo)
                    Text(
                        if (bpm > 0) "$bpm" else "--",
                        fontFamily = Fredoka, fontWeight = FontWeight.SemiBold, fontSize = 50.sp, color = Color.White,
                    )
                    Text(if (midiendo) "lpm · en vivo" else "lpm", fontSize = 12.sp, color = LilaTexto)
                    if (bpm > 0) {
                        Text(
                            nivel.texto,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = nivel.color,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFF1C1530))
                                .padding(horizontal = 10.dp, vertical = 3.dp),
                        )
                    }
                    CompactChip(
                        onClick = { midiendo = !midiendo },
                        label = { Text(if (midiendo) "Detener" else "Medir", fontWeight = FontWeight.Bold) },
                        colors = ChipDefaults.chipColors(backgroundColor = Lila, contentColor = Morado),
                    )
                    val estado = when (celular) {
                        true -> "Enviando al celular"
                        false -> "Celular no encontrado"
                        null -> "Abre VitaRing en el celular"
                    }
                    Text(estado, fontSize = 10.sp, color = LilaTexto)
                }
            }
        }
    }
}

/** Lee el sensor de ritmo cardíaco del reloj. */
private class SensorPulso(context: Context) {
    private val manager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensor: Sensor? = manager.getDefaultSensor(Sensor.TYPE_HEART_RATE)

    val disponible: Boolean get() = sensor != null

    fun lecturas(): Flow<Int> = callbackFlow {
        val s = sensor
        if (s == null) {
            close()
            return@callbackFlow
        }
        val oyente = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val valor = event.values.firstOrNull()?.roundToInt() ?: 0
                if (valor > 0) trySend(valor)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }
        manager.registerListener(oyente, s, SensorManager.SENSOR_DELAY_NORMAL)
        awaitClose { manager.unregisterListener(oyente) }
    }
}

/** Envía el pulso al celular emparejado mediante la Data Layer de Wear OS. */
private class EnviadorCelular(context: Context) {
    private val mensajes = Wearable.getMessageClient(context)
    private val nodosCliente = Wearable.getNodeClient(context)
    private var nodos: List<Node> = emptyList()
    private var ultimaBusqueda = 0L

    suspend fun enviar(bpm: Int): Boolean = try {
        val ahora = System.currentTimeMillis()
        if (nodos.isEmpty() || ahora - ultimaBusqueda > 15_000) {
            nodos = nodosCliente.connectedNodes.await()
            ultimaBusqueda = ahora
        }
        nodos.forEach { nodo ->
            mensajes.sendMessage(nodo.id, RUTA_PULSO, bpm.toString().toByteArray()).await()
        }
        nodos.isNotEmpty()
    } catch (e: Exception) {
        nodos = emptyList()
        false
    }
}
