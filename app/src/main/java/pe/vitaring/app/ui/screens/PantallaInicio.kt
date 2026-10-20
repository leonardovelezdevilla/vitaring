package pe.vitaring.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.rounded.DirectionsWalk
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Thermostat
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pe.vitaring.app.data.Config
import pe.vitaring.app.data.HealthRepository
import pe.vitaring.app.data.NivelPulso
import pe.vitaring.app.data.estadisticas
import pe.vitaring.app.ui.IconoCirculo
import pe.vitaring.app.ui.OndaPulso
import pe.vitaring.app.ui.Pildora
import pe.vitaring.app.ui.Rutas
import pe.vitaring.app.ui.Tarjeta
import pe.vitaring.app.ui.fechaHoy
import pe.vitaring.app.ui.miles
import pe.vitaring.app.ui.theme.Blanco
import pe.vitaring.app.ui.theme.Fredoka
import pe.vitaring.app.ui.theme.LilaSuave
import pe.vitaring.app.ui.theme.LilaTexto
import pe.vitaring.app.ui.theme.Morado
import pe.vitaring.app.ui.theme.MoradoTexto
import pe.vitaring.app.ui.theme.Rosa
import pe.vitaring.app.ui.theme.RosaAcento
import pe.vitaring.app.ui.theme.RosaBrillo
import pe.vitaring.app.ui.theme.RosaSuave
import pe.vitaring.app.ui.theme.RosaTinta
import pe.vitaring.app.ui.theme.TextoSuave
import pe.vitaring.app.ui.theme.VerdeOk
import pe.vitaring.app.ui.theme.estiloNumero

@Composable
fun PantallaInicio(navegar: (String) -> Unit) {
    val bpm by HealthRepository.bpm.collectAsStateWithLifecycle()
    val lecturas by HealthRepository.lecturas.collectAsStateWithLifecycle()
    val pasos by HealthRepository.pasos.collectAsStateWithLifecycle()
    val relojEnVivo by HealthRepository.relojEnVivo.collectAsStateWithLifecycle()
    val modoDemo by HealthRepository.modoDemo.collectAsStateWithLifecycle()
    val nivel = NivelPulso.para(bpm)
    val stats = estadisticas(lecturas)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Saludo
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(fechaHoy(), style = MaterialTheme.typography.labelMedium, color = TextoSuave)
                Text("Hola, ${Config.NOMBRE_USUARIO}", style = MaterialTheme.typography.headlineMedium, color = MoradoTexto)
            }
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(RosaBrillo),
                contentAlignment = Alignment.Center,
            ) {
                Text(Config.NOMBRE_USUARIO.take(1), fontFamily = Fredoka, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Morado)
            }
        }

        // Estado de conexión
        val (textoEstado, colorPunto) = when {
            relojEnVivo -> "Galaxy Watch conectado · en vivo" to VerdeOk
            modoDemo -> "Modo demo · datos simulados" to Color(0xFFE9A92B)
            else -> "Sin dispositivo conectado" to Color(0xFFC8324A)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(LilaSuave)
                .clickable { navegar(Rutas.ANILLO) }
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(10.dp).clip(CircleShape).background(colorPunto))
            Spacer(Modifier.width(10.dp))
            Text(textoEstado, style = MaterialTheme.typography.titleMedium, color = Morado)
        }

        // Tarjeta principal del pulso
        Tarjeta(fondo = Morado, conBorde = false, relleno = 20.dp, onClick = { navegar(Rutas.RITMO) }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Favorite, contentDescription = null, tint = RosaBrillo, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Ritmo cardíaco", style = MaterialTheme.typography.labelLarge, color = Rosa, modifier = Modifier.weight(1f))
                Pildora("● ${nivel.nombre} · ${nivel.etiqueta}", nivel.fondo, nivel.tinta)
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Text("$bpm", style = estiloNumero(64, Blanco))
                Spacer(Modifier.width(6.dp))
                Text("lpm", style = MaterialTheme.typography.titleMedium, color = Rosa, modifier = Modifier.padding(bottom = 10.dp))
                Spacer(Modifier.weight(1f))
                Text(
                    if (relojEnVivo) "en vivo" else "simulado",
                    style = MaterialTheme.typography.labelMedium,
                    color = LilaTexto,
                    modifier = Modifier.padding(bottom = 10.dp),
                )
            }
            OndaPulso(color = RosaBrillo, modifier = Modifier.fillMaxWidth().height(56.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf(
                    "Mín ${stats.minimo}", "Prom ${stats.promedio}", "Máx ${stats.maximo}", "VFC ${HealthRepository.VFC_DEMO} ms",
                ).forEach { Text(it, style = MaterialTheme.typography.labelMedium, color = LilaTexto) }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Tus métricas de hoy", style = MaterialTheme.typography.titleLarge, color = MoradoTexto, modifier = Modifier.weight(1f))
            TextButton(onClick = { navegar(Rutas.BIENESTAR) }) {
                Text("Ver todo", style = MaterialTheme.typography.labelLarge, color = Morado)
            }
        }

        val verde = Color(0xFF1A7A50)
        FilaMetricas(
            { m -> Metrica(m, Icons.Rounded.WaterDrop, true, "Oxígeno SpO₂", "${HealthRepository.SPO2_DEMO}", "%", "● Normal", verde) { navegar(Rutas.BIENESTAR) } },
            { m -> Metrica(m, Icons.Rounded.Psychology, false, "Estrés", "${HealthRepository.ESTRES_DEMO}", "/100", "● Bajo", verde) { navegar(Rutas.BIENESTAR) } },
        )
        FilaMetricas(
            { m ->
                Metrica(
                    m, Icons.AutoMirrored.Rounded.DirectionsWalk, true, "Pasos", miles(pasos), null,
                    "${(pasos * 100 / Config.META_PASOS).coerceAtMost(100)}% de ${miles(Config.META_PASOS)}", TextoSuave,
                    progreso = pasos.toFloat() / Config.META_PASOS,
                ) { navegar(Rutas.ACTIVIDAD) }
            },
            { m -> Metrica(m, Icons.Rounded.Bedtime, false, "Sueño", "7 h 24", "m", "● Buen descanso", verde) { navegar(Rutas.BIENESTAR) } },
        )
        FilaMetricas(
            { m -> Metrica(m, Icons.Rounded.Thermostat, true, "Temperatura", HealthRepository.TEMPERATURA_DEMO, "°C", "● Normal", verde) { navegar(Rutas.BIENESTAR) } },
            { m -> Metrica(m, Icons.Rounded.LocalFireDepartment, false, "Calorías", "${(pasos * 0.045).toInt()}", "kcal", "activas hoy", TextoSuave) { navegar(Rutas.ACTIVIDAD) } },
        )
    }
}

@Composable
private fun FilaMetricas(
    izquierda: @Composable (Modifier) -> Unit,
    derecha: @Composable (Modifier) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        izquierda(Modifier.weight(1f).fillMaxHeight())
        derecha(Modifier.weight(1f).fillMaxHeight())
    }
}

@Composable
private fun Metrica(
    modifier: Modifier,
    icono: ImageVector,
    rosa: Boolean,
    titulo: String,
    valor: String,
    unidad: String?,
    estado: String,
    colorEstado: Color,
    progreso: Float? = null,
    onClick: () -> Unit,
) {
    Tarjeta(modifier = modifier, relleno = 14.dp, onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (rosa) IconoCirculo(icono, RosaSuave, RosaTinta, 28.dp) else IconoCirculo(icono, LilaSuave, Morado, 28.dp)
            Spacer(Modifier.width(8.dp))
            Text(titulo, style = MaterialTheme.typography.labelMedium, color = TextoSuave)
        }
        Row(verticalAlignment = Alignment.Bottom) {
            Text(valor, style = estiloNumero(28))
            if (unidad != null) {
                Text(" $unidad", style = estiloNumero(16), modifier = Modifier.padding(bottom = 4.dp))
            }
        }
        if (progreso != null) {
            LinearProgressIndicator(
                progress = { progreso.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = RosaAcento,
                trackColor = Color(0xFFF2EAFB),
                strokeCap = StrokeCap.Round,
                gapSize = 0.dp,
                drawStopIndicator = {},
            )
        }
        Text(estado, style = MaterialTheme.typography.labelMedium, color = colorEstado)
    }
}
