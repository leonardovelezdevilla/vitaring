package pe.vitaring.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsRun
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import pe.vitaring.app.data.Config
import pe.vitaring.app.data.HealthRepository
import pe.vitaring.app.ui.IconoCirculo
import pe.vitaring.app.ui.Pildora
import pe.vitaring.app.ui.ProgresoCircular
import pe.vitaring.app.ui.Tarjeta
import pe.vitaring.app.ui.miles
import pe.vitaring.app.ui.theme.Blanco
import pe.vitaring.app.ui.theme.Lila
import pe.vitaring.app.ui.theme.LilaSuave
import pe.vitaring.app.ui.theme.Morado
import pe.vitaring.app.ui.theme.MoradoTexto
import pe.vitaring.app.ui.theme.RosaAcento
import pe.vitaring.app.ui.theme.RosaSuave
import pe.vitaring.app.ui.theme.RosaTinta
import pe.vitaring.app.ui.theme.TextoSuave
import pe.vitaring.app.ui.theme.estiloNumero
import java.time.LocalDate
import java.util.Locale

@Composable
fun PantallaActividad() {
    val pasos by HealthRepository.pasos.collectAsStateWithLifecycle()
    val podometroReal by HealthRepository.podometroReal.collectAsStateWithLifecycle()
    val modoDemo by HealthRepository.modoDemo.collectAsStateWithLifecycle()
    val bpm by HealthRepository.bpm.collectAsStateWithLifecycle()

    var entrenando by remember { mutableStateOf(false) }
    var inicio by remember { mutableLongStateOf(0L) }
    var pasosInicio by remember { mutableIntStateOf(0) }
    var ahora by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(entrenando) {
        while (entrenando) {
            ahora = System.currentTimeMillis()
            delay(1_000)
        }
    }

    val km = pasos * 0.00072
    val kcal = (pasos * 0.045).toInt()
    val minutosActivos = pasos / 150

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Actividad", style = MaterialTheme.typography.headlineSmall, color = MoradoTexto, modifier = Modifier.weight(1f))
            Text("Hoy", style = MaterialTheme.typography.labelLarge, color = TextoSuave)
        }

        Tarjeta(relleno = 18.dp) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                ProgresoCircular(
                    progreso = pasos.toFloat() / Config.META_PASOS,
                    color = RosaAcento,
                    pista = Color(0xFFF2EAFB),
                    grosor = 18.dp,
                    modifier = Modifier.size(190.dp),
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(miles(pasos), style = estiloNumero(40, Morado))
                    Text("de ${miles(Config.META_PASOS)} pasos", style = MaterialTheme.typography.labelMedium, color = TextoSuave)
                }
            }
            val fuente = when {
                podometroReal && modoDemo -> "Podómetro del celular + base demo"
                podometroReal -> "Podómetro del celular"
                else -> "Pasos de demostración"
            }
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Pildora(fuente, LilaSuave, Morado)
            }
            Row(Modifier.fillMaxWidth()) {
                Dato(String.format(Locale.US, "%.1f", km), "km", Modifier.weight(1f))
                Dato("$kcal", "kcal", Modifier.weight(1f))
                Dato("$minutosActivos", "min activos", Modifier.weight(1f))
            }
        }

        // Semana (días pasados con datos de ejemplo; hoy con datos reales)
        Tarjeta {
            Text("Esta semana", style = MaterialTheme.typography.titleMedium, color = MoradoTexto)
            val hoy = LocalDate.now().dayOfWeek.value - 1 // 0 = lunes
            val ejemplo = listOf(8_200, 7_400, 9_100, 6_300, 10_400, 5_200, 7_800)
            val valores = ejemplo.mapIndexed { i, v -> if (i == hoy) pasos else if (i < hoy) v else 0 }
            Row(
                modifier = Modifier.fillMaxWidth().height(90.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                valores.forEachIndexed { i, v ->
                    val fraccion = (v.toFloat() / 11_000f).coerceIn(0.08f, 1f)
                    Box(
                        Modifier
                            .weight(1f)
                            .fillMaxHeight(fraccion)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when {
                                    i == hoy -> Morado
                                    i < hoy -> Lila
                                    else -> Color(0xFFF2EAFB)
                                },
                            ),
                    )
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("L", "M", "M", "J", "V", "S", "D").forEachIndexed { i, d ->
                    Text(
                        d,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (i == hoy) Morado else TextoSuave,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        // Entrenamiento
        Tarjeta(relleno = 14.dp) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconoCirculo(Icons.AutoMirrored.Rounded.DirectionsRun, RosaSuave, RosaTinta, 44.dp)
                Spacer(Modifier.width(12.dp))
                if (entrenando) {
                    val segundos = ((ahora - inicio) / 1000).toInt()
                    Column(Modifier.weight(1f)) {
                        Text("Entrenamiento en curso", style = MaterialTheme.typography.titleMedium, color = MoradoTexto)
                        Text(
                            String.format(Locale.US, "%02d:%02d · %d pasos", segundos / 60, segundos % 60, (pasos - pasosInicio).coerceAtLeast(0)),
                            style = MaterialTheme.typography.labelMedium,
                            color = TextoSuave,
                        )
                    }
                } else {
                    Column(Modifier.weight(1f)) {
                        Text("Caminata", style = MaterialTheme.typography.titleMedium, color = MoradoTexto)
                        Text("7:10 a. m. · 32 min · 3.1 km", style = MaterialTheme.typography.labelMedium, color = TextoSuave)
                    }
                }
                Text("$bpm lpm", style = estiloNumero(17, Morado))
            }
        }

        Button(
            onClick = {
                if (!entrenando) {
                    inicio = System.currentTimeMillis()
                    ahora = inicio
                    pasosInicio = pasos
                }
                entrenando = !entrenando
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Morado, contentColor = Blanco),
        ) {
            Icon(if (entrenando) Icons.Rounded.Stop else Icons.Rounded.PlayArrow, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(if (entrenando) "Terminar entrenamiento" else "Iniciar entrenamiento", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun Dato(valor: String, unidad: String, modifier: Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, style = estiloNumero(22))
        Text(unidad, style = MaterialTheme.typography.labelMedium, color = TextoSuave)
    }
}
