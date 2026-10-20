package pe.vitaring.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import pe.vitaring.app.data.HealthRepository
import pe.vitaring.app.data.PerfilRepo
import pe.vitaring.app.ui.IconoCirculo
import pe.vitaring.app.ui.MedidorSemicircular
import pe.vitaring.app.ui.Pildora
import pe.vitaring.app.ui.Tarjeta
import pe.vitaring.app.ui.theme.Blanco
import pe.vitaring.app.ui.theme.Lila
import pe.vitaring.app.ui.theme.LilaTexto
import pe.vitaring.app.ui.theme.Morado
import pe.vitaring.app.ui.theme.MoradoClaro
import pe.vitaring.app.ui.theme.MoradoTexto
import pe.vitaring.app.ui.theme.Rosa
import pe.vitaring.app.ui.theme.RosaAcento
import pe.vitaring.app.ui.theme.RosaBrillo
import pe.vitaring.app.ui.theme.RosaSuave
import pe.vitaring.app.ui.theme.TextoSuave
import pe.vitaring.app.ui.theme.estiloNumero
import java.time.LocalDate
import java.util.Locale

private val VerdeTexto = Color(0xFF1A7A50)

@Composable
fun PantallaBienestar() {
    var respirando by remember { mutableStateOf(false) }
    val perfil by PerfilRepo.perfil.collectAsStateWithLifecycle()
    val metaSueno = perfil?.metaSuenoHoras ?: 8

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Bienestar", style = MaterialTheme.typography.headlineSmall, color = MoradoTexto, modifier = Modifier.weight(1f))
            Text("Hoy", style = MaterialTheme.typography.labelLarge, color = TextoSuave)
        }

        // Estrés
        Tarjeta(fondo = Morado, conBorde = false, relleno = 18.dp) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(width = 150.dp, height = 90.dp), contentAlignment = Alignment.BottomCenter) {
                    MedidorSemicircular(
                        progreso = HealthRepository.ESTRES_DEMO / 100f,
                        color = Rosa,
                        pista = MoradoClaro,
                        grosor = 16.dp,
                        modifier = Modifier.size(150.dp).padding(top = 0.dp).align(Alignment.TopCenter),
                    )
                    Text("${HealthRepository.ESTRES_DEMO}", style = estiloNumero(34, Blanco))
                }
                Spacer(Modifier.width(14.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Nivel de estrés", style = MaterialTheme.typography.labelLarge, color = Rosa)
                    Pildora("Bajo · relajado", Color(0xFFCDEFD9), Color(0xFF145C3B))
                    Text("Calculado con la variabilidad de tu pulso (VFC)", style = MaterialTheme.typography.bodySmall, color = LilaTexto)
                }
            }
        }

        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Indicador("Oxígeno SpO₂", "${HealthRepository.SPO2_DEMO}", "%", "● Normal (95–100%)", Modifier.weight(1f).fillMaxHeight())
            Indicador("Respiración", "${HealthRepository.RESPIRACION_DEMO}", "rpm", "● Normal", Modifier.weight(1f).fillMaxHeight())
        }
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Indicador("Temperatura", HealthRepository.TEMPERATURA_DEMO, "°C", "● Normal", Modifier.weight(1f).fillMaxHeight())
            Indicador("VFC", "${HealthRepository.VFC_DEMO}", "ms", "● Buena recuperación", Modifier.weight(1f).fillMaxHeight())
        }

        // Sueño
        Tarjeta {
            Row(verticalAlignment = Alignment.Bottom) {
                Text("Sueño de anoche", style = MaterialTheme.typography.titleMedium, color = MoradoTexto, modifier = Modifier.weight(1f))
                Text("7 h 24 m", style = estiloNumero(22, Morado))
            }
            val fases = listOf(
                Triple("Profundo", "1 h 32 m", 92f) to Morado,
                Triple("Ligero", "4 h 10 m", 250f) to Lila,
                Triple("REM", "1 h 22 m", 82f) to Color(0xFFE07AB0),
                Triple("Despierto", "20 m", 20f) to Color(0xFFE9A92B),
            )
            Row(
                Modifier.fillMaxWidth().height(18.dp).clip(RoundedCornerShape(9.dp)),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                fases.forEach { (f, color) -> Box(Modifier.weight(f.third).fillMaxHeight().background(color)) }
            }
            fases.chunked(2).forEach { fila ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    fila.forEach { (f, color) ->
                        Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(10.dp).clip(RoundedCornerShape(3.dp)).background(color))
                            Spacer(Modifier.width(8.dp))
                            Text(f.first, style = MaterialTheme.typography.labelMedium, color = MoradoTexto, modifier = Modifier.weight(1f))
                            Text(f.second, style = MaterialTheme.typography.labelMedium, color = TextoSuave)
                        }
                    }
                }
            }
        }

        SuenoSemana(metaSueno)

        // Respiración guiada
        Tarjeta(fondo = Rosa, conBorde = false, relleno = 14.dp) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconoCirculo(Icons.Rounded.SelfImprovement, Blanco, Morado, 44.dp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Respira 1 minuto", style = MaterialTheme.typography.titleMedium, color = Morado)
                    Text("Ejercicio guiado para bajar el estrés", style = MaterialTheme.typography.bodySmall, color = Morado)
                }
                Button(
                    onClick = { respirando = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Morado, contentColor = Blanco),
                ) { Text("Iniciar", style = MaterialTheme.typography.labelLarge) }
            }
        }
    }

    if (respirando) DialogoRespiracion(onCerrar = { respirando = false })
}

@Composable
private fun Indicador(titulo: String, valor: String, unidad: String, estado: String, modifier: Modifier) {
    Tarjeta(modifier = modifier, relleno = 14.dp) {
        Text(titulo, style = MaterialTheme.typography.labelMedium, color = TextoSuave)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(valor, style = estiloNumero(30))
            Text(" $unidad", style = estiloNumero(16), modifier = Modifier.padding(bottom = 4.dp))
        }
        Text(estado, style = MaterialTheme.typography.labelMedium, color = VerdeTexto)
    }
}

@Composable
private fun DialogoRespiracion(onCerrar: () -> Unit) {
    var inhala by remember { mutableStateOf(true) }
    var restante by remember { mutableIntStateOf(60) }
    LaunchedEffect(Unit) {
        while (restante > 0) {
            delay(1_000)
            restante--
            if (restante % 4 == 0) inhala = !inhala
        }
    }
    val escala by animateFloatAsState(if (inhala) 1f else 0.6f, tween(4_000), label = "respiracion")

    Dialog(onDismissRequest = onCerrar) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(28.dp))
                .background(Blanco)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Respira conmigo", style = MaterialTheme.typography.headlineSmall, color = MoradoTexto)
            Box(Modifier.size(200.dp), contentAlignment = Alignment.Center) {
                Box(Modifier.size(200.dp).scale(escala).clip(CircleShape).background(RosaSuave))
                Box(Modifier.size(120.dp).scale(escala).clip(CircleShape).background(RosaBrillo))
                Text(if (restante == 0) "¡Listo!" else if (inhala) "Inhala" else "Exhala", style = estiloNumero(26, Morado))
            }
            Text("$restante s", style = MaterialTheme.typography.labelLarge, color = TextoSuave, textAlign = TextAlign.Center)
            TextButton(onClick = onCerrar) { Text("Cerrar", style = MaterialTheme.typography.labelLarge, color = Morado) }
        }
    }
}

/** Gráfico de barras con las horas de sueño de la semana y la meta del perfil. */
@Composable
private fun SuenoSemana(meta: Int) {
    val hoy = LocalDate.now().dayOfWeek.value - 1 // 0 = lunes
    // Noches de ejemplo (hasta tener el anillo físico); la de hoy es la de anoche: 7 h 24 m
    val ejemplo = listOf(7.1f, 6.4f, 8.2f, 6.9f, 5.8f, 8.6f, 7.7f)
    val horas = ejemplo.mapIndexed { i, v -> if (i == hoy) 7.4f else if (i < hoy) v else 0f }
    val noches = horas.take(hoy + 1)
    val promedio = noches.average().toFloat()
    val cumplidas = noches.count { it >= meta }
    val escala = 10f

    Tarjeta {
        Row(verticalAlignment = Alignment.Bottom) {
            Text("Horas de sueño esta semana", style = MaterialTheme.typography.titleMedium, color = MoradoTexto, modifier = Modifier.weight(1f))
            Pildora("Meta $meta h", RosaSuave, RosaAcento)
        }
        Canvas(Modifier.fillMaxWidth().height(130.dp)) {
            val n = horas.size
            val hueco = 12.dp.toPx()
            val ancho = (size.width - hueco * (n - 1)) / n
            horas.forEachIndexed { i, h ->
                val alto = if (h == 0f) 6.dp.toPx() else size.height * (h / escala).coerceIn(0f, 1f)
                val color = when {
                    h == 0f -> Color(0xFFF2EAFB)
                    h >= meta -> Morado
                    else -> Lila
                }
                drawRoundRect(
                    color = color,
                    topLeft = Offset(i * (ancho + hueco), size.height - alto),
                    size = Size(ancho, alto),
                    cornerRadius = CornerRadius(8.dp.toPx()),
                )
            }
            val y = size.height * (1f - meta / escala)
            drawLine(
                color = RosaAcento,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f)),
            )
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
        Text(
            "Promedio ${String.format(Locale.US, "%.1f", promedio)} h · cumpliste tu meta $cumplidas de ${noches.size} noches",
            style = MaterialTheme.typography.labelMedium,
            color = TextoSuave,
        )
    }
}
