package pe.vitaring.app.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pe.vitaring.app.data.HealthRepository
import pe.vitaring.app.data.NivelPulso
import pe.vitaring.app.data.PerfilRepo
import pe.vitaring.app.data.estadisticas
import pe.vitaring.app.ui.Encabezado
import pe.vitaring.app.ui.Rutas
import pe.vitaring.app.ui.Tarjeta
import pe.vitaring.app.ui.theme.Blanco
import pe.vitaring.app.ui.theme.Lila
import pe.vitaring.app.ui.theme.LilaSuave
import pe.vitaring.app.ui.theme.Morado
import pe.vitaring.app.ui.theme.MoradoTexto
import pe.vitaring.app.ui.theme.RosaAcento
import pe.vitaring.app.ui.theme.RosaSuave
import pe.vitaring.app.ui.theme.TextoSuave
import pe.vitaring.app.ui.theme.estiloNumero

@Composable
fun PantallaRitmo(navegar: (String) -> Unit) {
    val bpm by HealthRepository.bpm.collectAsStateWithLifecycle()
    val lecturas by HealthRepository.lecturas.collectAsStateWithLifecycle()
    val relojEnVivo by HealthRepository.relojEnVivo.collectAsStateWithLifecycle()
    val perfil by PerfilRepo.perfil.collectAsStateWithLifecycle()
    val nivel = NivelPulso.para(bpm)
    val stats = estadisticas(lecturas)
    var periodo by remember { mutableIntStateOf(0) }

    // Corazón que late
    val latido by rememberInfiniteTransition(label = "latido").animateFloat(
        initialValue = 1f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(tween(400), RepeatMode.Reverse),
        label = "escala",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Encabezado("Ritmo cardíaco", if (relojEnVivo) "Datos en vivo del Galaxy Watch" else "Datos simulados (modo demo)")

        // Selector Día / Semana / Mes
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(LilaSuave)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            listOf("Día", "Semana", "Mes").forEachIndexed { i, texto ->
                val activo = periodo == i
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (activo) Morado else Color.Transparent)
                        .clickable { periodo = i },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(texto, style = MaterialTheme.typography.labelLarge, color = if (activo) Blanco else Morado)
                }
            }
        }

        // Círculo con el pulso actual (el borde toma el color del semáforo)
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier.size(200.dp).clip(CircleShape).background(RosaSuave),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier = Modifier
                        .size(168.dp)
                        .clip(CircleShape)
                        .background(Blanco)
                        .border(10.dp, nivel.colorLuz, CircleShape),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(Icons.Rounded.Favorite, contentDescription = null, tint = RosaAcento, modifier = Modifier.size(22.dp).scale(latido))
                    Text("$bpm", style = estiloNumero(52, Morado))
                    Text("lpm · ${nivel.etiqueta}", style = MaterialTheme.typography.labelLarge, color = TextoSuave)
                }
            }
        }

        // Estadísticas
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(
                "Mín" to "${stats.minimo}", "Prom" to "${stats.promedio}",
                "Máx" to "${stats.maximo}", "VFC" to "${HealthRepository.VFC_DEMO} ms",
            ).forEach { (titulo, valor) ->
                Tarjeta(modifier = Modifier.weight(1f), relleno = 10.dp) {
                    Text(titulo, style = MaterialTheme.typography.labelMedium, color = TextoSuave, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    Text(valor, style = estiloNumero(19), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }
            }
        }

        // Pulso máximo según la edad (220 − edad)
        perfil?.let { p ->
            val maximo = p.pulsoMaximo
            val porcentaje = (bpm * 100 / maximo).coerceIn(0, 100)
            Tarjeta(fondo = LilaSuave, conBorde = false) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Tu pulso máximo", style = MaterialTheme.typography.titleMedium, color = MoradoTexto)
                        Text("220 − ${p.edad} años", style = MaterialTheme.typography.labelMedium, color = TextoSuave)
                    }
                    Text("$maximo lpm", style = estiloNumero(24, Morado))
                }
                LinearProgressIndicator(
                    progress = { porcentaje / 100f },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = nivel.colorLuz,
                    trackColor = Blanco,
                    strokeCap = StrokeCap.Round,
                    gapSize = 0.dp,
                    drawStopIndicator = {},
                )
                Text(
                    "Ahora estás al $porcentaje% de tu máximo",
                    style = MaterialTheme.typography.labelMedium,
                    color = Morado,
                )
            }
        }

        // Gráfico por hora (datos de ejemplo)
        Tarjeta {
            Row {
                Text("Durante el día", style = MaterialTheme.typography.titleMedium, color = MoradoTexto, modifier = Modifier.weight(1f))
                Text("lpm por hora", style = MaterialTheme.typography.labelMedium, color = TextoSuave)
            }
            val alturas = listOf(0.38f, 0.34f, 0.36f, 0.52f, 0.58f, 0.55f, 0.6f, 0.57f, 0.62f, 1f, 0.64f, 0.48f)
            Row(
                modifier = Modifier.fillMaxWidth().height(96.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                alturas.forEach { h ->
                    Box(
                        Modifier
                            .weight(1f)
                            .fillMaxHeight(h)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (h == 1f) RosaAcento else Lila),
                    )
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("0 h", "6 h", "12 h", "18 h", "24 h").forEach {
                    Text(it, style = MaterialTheme.typography.labelSmall, color = TextoSuave)
                }
            }
        }

        // Semáforo
        Tarjeta {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Semáforo de tu pulso", style = MaterialTheme.typography.titleMedium, color = MoradoTexto, modifier = Modifier.weight(1f))
                TextButton(onClick = { navegar(Rutas.RESULTADO) }) {
                    Text("Medir ahora", style = MaterialTheme.typography.labelLarge, color = Morado)
                }
            }
            NivelPulso.entries.forEach { n -> FilaSemaforo(n, actual = n == nivel) }
        }
    }
}

@Composable
fun FilaSemaforo(nivel: NivelPulso, actual: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (actual) LilaSuave else Color.Transparent)
            .then(if (actual) Modifier.border(2.dp, Lila, RoundedCornerShape(14.dp)) else Modifier)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(20.dp).clip(CircleShape).background(nivel.colorLuz))
        Spacer(Modifier.width(12.dp))
        Text("${nivel.nombre} · ${nivel.etiqueta}", style = MaterialTheme.typography.titleMedium, color = MoradoTexto, modifier = Modifier.weight(1f))
        Text(nivel.rango, style = MaterialTheme.typography.labelMedium, color = TextoSuave)
    }
}
