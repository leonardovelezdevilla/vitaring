package pe.vitaring.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Watch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pe.vitaring.app.data.HealthRepository
import pe.vitaring.app.ui.IconoCirculo
import pe.vitaring.app.ui.Pildora
import pe.vitaring.app.ui.Rutas
import pe.vitaring.app.ui.Tarjeta
import pe.vitaring.app.ui.theme.Blanco
import pe.vitaring.app.ui.theme.Borde
import pe.vitaring.app.ui.theme.Fredoka
import pe.vitaring.app.ui.theme.Lila
import pe.vitaring.app.ui.theme.LilaSuave
import pe.vitaring.app.ui.theme.Morado
import pe.vitaring.app.ui.theme.MoradoTexto
import pe.vitaring.app.ui.theme.Rosa
import pe.vitaring.app.ui.theme.RosaAcento
import pe.vitaring.app.ui.theme.RosaSuave
import pe.vitaring.app.ui.theme.RosaTinta
import pe.vitaring.app.ui.theme.TextoSuave

@Composable
fun PantallaAnillo(navegar: (String) -> Unit) {
    val contexto = LocalContext.current
    val relojEnVivo by HealthRepository.relojEnVivo.collectAsStateWithLifecycle()
    val modoDemo by HealthRepository.modoDemo.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Mi anillo", style = MaterialTheme.typography.headlineSmall, color = MoradoTexto)

        // Tarjeta del anillo
        Tarjeta(fondo = Rosa, conBorde = false, relleno = 20.dp) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(112.dp), contentAlignment = Alignment.Center) {
                    Canvas(Modifier.size(112.dp)) {
                        val g = 16.dp.toPx()
                        drawCircle(Morado, radius = size.minDimension / 2 - g / 2, style = Stroke(g))
                        drawLine(
                            Lila,
                            start = center.copy(x = center.x - 12.dp.toPx(), y = size.height - g / 2),
                            end = center.copy(x = center.x + 12.dp.toPx(), y = size.height - g / 2),
                            strokeWidth = 10.dp.toPx(),
                            cap = StrokeCap.Round,
                        )
                    }
                    Icon(Icons.Rounded.Favorite, contentDescription = null, tint = RosaAcento, modifier = Modifier.size(34.dp))
                }
                Spacer(Modifier.width(18.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("VitaRing", fontFamily = Fredoka, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Morado)
                    Text(
                        when {
                            relojEnVivo -> "Recibiendo datos del reloj"
                            modoDemo -> "Prototipo · modo demo"
                            else -> "Sin conexión"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = Morado,
                    )
                    Text("Batería ${HealthRepository.BATERIA_DEMO}%", style = MaterialTheme.typography.labelMedium, color = Morado)
                    LinearProgressIndicator(
                        progress = { HealthRepository.BATERIA_DEMO / 100f },
                        modifier = Modifier.width(140.dp).height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = Morado,
                        trackColor = Blanco,
                        strokeCap = StrokeCap.Round,
                        gapSize = 0.dp,
                        drawStopIndicator = {},
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = { navegar(Rutas.RESULTADO) },
                modifier = Modifier.weight(1f).height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Morado, contentColor = Blanco),
            ) { Text("Medir ahora", style = MaterialTheme.typography.labelLarge) }
            OutlinedButton(
                onClick = { Toast.makeText(contexto, "Datos sincronizados", Toast.LENGTH_SHORT).show() },
                modifier = Modifier.weight(1f).height(52.dp),
                border = BorderStroke(2.dp, Lila),
            ) { Text("Sincronizar", style = MaterialTheme.typography.labelLarge, color = Morado) }
        }

        // Sensores
        Tarjeta(relleno = 16.dp) {
            Text("Sensores del anillo", style = MaterialTheme.typography.titleMedium, color = MoradoTexto)
            listOf(
                "Pulso y VFC (óptico PPG)",
                "Oxígeno en sangre (SpO₂)",
                "Acelerómetro (pasos y sueño)",
                "Temperatura de la piel",
            ).forEachIndexed { i, sensor ->
                if (i > 0) HorizontalDivider(color = Borde)
                Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(sensor, style = MaterialTheme.typography.bodyMedium, color = MoradoTexto, modifier = Modifier.weight(1f))
                    Text("Activo", style = MaterialTheme.typography.labelMedium, color = Color(0xFF1A7A50))
                }
            }
        }

        // Fuentes de datos
        Tarjeta(relleno = 16.dp) {
            Text("Fuentes de datos", style = MaterialTheme.typography.titleMedium, color = MoradoTexto)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconoCirculo(Icons.Rounded.Watch, LilaSuave, Morado, 40.dp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Galaxy Watch · Wear OS", style = MaterialTheme.typography.titleMedium, color = MoradoTexto)
                    Text("Abre VitaRing en el reloj y pulsa Medir", style = MaterialTheme.typography.bodySmall, color = TextoSuave)
                }
                if (relojEnVivo) {
                    Pildora("En vivo", Color(0xFFDDF3E6), Color(0xFF145C3B))
                } else {
                    Pildora("Esperando", LilaSuave, TextoSuave)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconoCirculo(Icons.Rounded.PlayArrow, RosaSuave, RosaTinta, 40.dp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Modo demo", style = MaterialTheme.typography.titleMedium, color = MoradoTexto)
                    Text("Datos simulados para presentar", style = MaterialTheme.typography.bodySmall, color = TextoSuave)
                }
                Switch(
                    checked = modoDemo,
                    onCheckedChange = { HealthRepository.cambiarModoDemo(it) },
                    colors = SwitchDefaults.colors(checkedTrackColor = Morado, checkedThumbColor = Blanco),
                )
            }
        }

        Text(
            "Mediciones referenciales. VitaRing no es un dispositivo médico.",
            style = MaterialTheme.typography.bodySmall,
            color = TextoSuave,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Suppress("unused")
private val circulo = CircleShape
