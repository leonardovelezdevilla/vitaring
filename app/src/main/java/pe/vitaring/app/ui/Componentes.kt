package pe.vitaring.app.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import pe.vitaring.app.ui.theme.Blanco
import pe.vitaring.app.ui.theme.Borde
import pe.vitaring.app.ui.theme.Morado
import pe.vitaring.app.ui.theme.MoradoTexto
import pe.vitaring.app.ui.theme.TextoSuave
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Tarjeta blanca redondeada, base de casi todo el diseño. */
@Composable
fun Tarjeta(
    modifier: Modifier = Modifier,
    fondo: Color = Blanco,
    conBorde: Boolean = true,
    relleno: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    contenido: @Composable ColumnScope.() -> Unit,
) {
    val forma = RoundedCornerShape(22.dp)
    var m = modifier.clip(forma).background(fondo)
    if (conBorde) m = m.border(1.dp, Borde, forma)
    if (onClick != null) m = m.clickable(onClick = onClick)
    Column(
        modifier = m.padding(relleno),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = contenido,
    )
}

/** Etiqueta en forma de píldora (ej. "● Normal"). */
@Composable
fun Pildora(texto: String, fondo: Color, tinta: Color, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        style = MaterialTheme.typography.labelMedium,
        color = tinta,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(fondo)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}

@Composable
fun IconoCirculo(icono: ImageVector, fondo: Color, tinta: Color, tamano: Dp = 30.dp) {
    Box(
        modifier = Modifier.size(tamano).clip(CircleShape).background(fondo),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icono, contentDescription = null, tint = tinta, modifier = Modifier.size(tamano * 0.56f))
    }
}

/** Título de pantalla con botón opcional de volver. */
@Composable
fun Encabezado(titulo: String, subtitulo: String? = null, onVolver: (() -> Unit)? = null) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (onVolver != null) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Blanco)
                    .border(1.dp, Borde, CircleShape)
                    .clickable(onClick = onVolver),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver", tint = Morado)
            }
            Spacer(Modifier.width(12.dp))
        }
        Column {
            Text(titulo, style = MaterialTheme.typography.headlineSmall, color = MoradoTexto)
            if (subtitulo != null) {
                Text(subtitulo, style = MaterialTheme.typography.labelMedium, color = TextoSuave)
            }
        }
    }
}

/** Onda tipo electrocardiograma que se desplaza continuamente. */
@Composable
fun OndaPulso(color: Color, modifier: Modifier = Modifier) {
    val transicion = rememberInfiniteTransition(label = "onda")
    val avance by transicion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1_000, easing = LinearEasing), RepeatMode.Restart),
        label = "avance",
    )
    // Un latido: puntos (x, y) normalizados de 0 a 1
    val latido = listOf(
        0f to 0.6f, 0.2f to 0.6f, 0.26f to 0.48f, 0.32f to 0.6f, 0.42f to 0.6f,
        0.47f to 0.06f, 0.53f to 0.96f, 0.58f to 0.6f, 0.72f to 0.6f,
        0.8f to 0.46f, 0.88f to 0.6f, 1f to 0.6f,
    )
    Canvas(modifier = modifier) {
        val anchoLatido = 110.dp.toPx()
        val desplazamiento = -avance * anchoLatido
        val ruta = Path()
        var i = -1
        var primero = true
        while (i * anchoLatido + desplazamiento < size.width + anchoLatido) {
            val inicioX = i * anchoLatido + desplazamiento
            for ((x, y) in latido) {
                val px = inicioX + x * anchoLatido
                val py = y * size.height
                if (primero) {
                    ruta.moveTo(px, py); primero = false
                } else {
                    ruta.lineTo(px, py)
                }
            }
            i++
        }
        drawPath(ruta, color, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

/** Anillo de progreso (pasos, metas). */
@Composable
fun ProgresoCircular(progreso: Float, color: Color, pista: Color, grosor: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val g = grosor.toPx()
        val tamano = androidx.compose.ui.geometry.Size(size.width - g, size.height - g)
        val origen = Offset(g / 2, g / 2)
        drawArc(pista, 0f, 360f, false, origen, tamano, style = Stroke(g))
        drawArc(color, -90f, 360f * progreso.coerceIn(0f, 1f), false, origen, tamano, style = Stroke(g, cap = StrokeCap.Round))
    }
}

/** Medidor semicircular (nivel de estrés). */
@Composable
fun MedidorSemicircular(progreso: Float, color: Color, pista: Color, grosor: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val g = grosor.toPx()
        val diametro = size.width - g
        val tamano = androidx.compose.ui.geometry.Size(diametro, diametro)
        val origen = Offset(g / 2, g / 2)
        drawArc(pista, 180f, 180f, false, origen, tamano, style = Stroke(g, cap = StrokeCap.Round))
        drawArc(color, 180f, 180f * progreso.coerceIn(0f, 1f), false, origen, tamano, style = Stroke(g, cap = StrokeCap.Round))
    }
}

fun fechaHoy(): String {
    val formato = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale.forLanguageTag("es-PE"))
    return LocalDate.now().format(formato).replaceFirstChar { it.uppercase() }
}

/** 6842 -> "6 842" */
fun miles(n: Int): String = n.toString().reversed().chunked(3).joinToString(" ").reversed()
