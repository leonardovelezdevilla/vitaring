package pe.vitaring.app.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import pe.vitaring.app.data.HealthRepository
import pe.vitaring.app.data.NivelPulso
import pe.vitaring.app.ui.Encabezado
import pe.vitaring.app.ui.Rutas
import pe.vitaring.app.ui.Tarjeta
import pe.vitaring.app.ui.theme.Blanco
import pe.vitaring.app.ui.theme.Fredoka
import pe.vitaring.app.ui.theme.Lila
import pe.vitaring.app.ui.theme.LilaSuave
import pe.vitaring.app.ui.theme.Morado
import pe.vitaring.app.ui.theme.MoradoTexto
import pe.vitaring.app.ui.theme.RosaAcento
import pe.vitaring.app.ui.theme.RosaSuave
import pe.vitaring.app.ui.theme.TextoSuave
import pe.vitaring.app.ui.theme.estiloNumero
import kotlin.math.roundToInt

private const val SEGUNDOS_MEDICION = 5

/** Mide el pulso unos segundos y muestra el semáforo (Verde / Amarillo / Rojo). */
@Composable
fun PantallaResultado(volver: () -> Unit, navegar: (String) -> Unit) {
    val bpmActual by HealthRepository.bpm.collectAsStateWithLifecycle()
    val relojEnVivo by HealthRepository.relojEnVivo.collectAsStateWithLifecycle()
    val modoDemo by HealthRepository.modoDemo.collectAsStateWithLifecycle()

    var intento by remember { mutableIntStateOf(0) }
    var midiendo by remember { mutableStateOf(true) }
    var segundos by remember { mutableIntStateOf(SEGUNDOS_MEDICION) }
    var resultado by remember { mutableIntStateOf(0) }

    LaunchedEffect(intento) {
        midiendo = true
        val muestras = mutableListOf<Int>()
        for (s in SEGUNDOS_MEDICION downTo 1) {
            segundos = s
            delay(1_000)
            muestras += HealthRepository.bpm.value
        }
        resultado = muestras.average().roundToInt()
        midiendo = false
    }

    val latido by rememberInfiniteTransition(label = "latido").animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(450), RepeatMode.Reverse),
        label = "escala",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Encabezado(
            "Resultado de tu pulso",
            if (relojEnVivo) "Medido con tu Galaxy Watch" else "Medición simulada (modo demo)",
            onVolver = volver,
        )

        if (midiendo) {
            Box(Modifier.fillMaxWidth().padding(top = 24.dp), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier.size(220.dp).scale(latido).clip(CircleShape).background(RosaSuave),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Rounded.Favorite, contentDescription = null, tint = RosaAcento, modifier = Modifier.size(48.dp))
                        Text("$bpmActual", style = estiloNumero(44, Morado))
                    }
                }
            }
            Text(
                "Midiendo tu pulso…",
                style = MaterialTheme.typography.headlineSmall,
                color = MoradoTexto,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                "Mantén la mano quieta · $segundos s",
                style = MaterialTheme.typography.bodyLarge,
                color = TextoSuave,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            val nivel = NivelPulso.para(resultado)

            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier.size(220.dp).clip(CircleShape).background(nivel.fondo),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        modifier = Modifier.size(176.dp).clip(CircleShape).background(Blanco).border(18.dp, nivel.colorLuz, CircleShape),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text("$resultado", style = estiloNumero(56))
                        Text("lpm", style = MaterialTheme.typography.labelLarge, color = TextoSuave)
                    }
                }
            }

            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(nivel.nombre, fontFamily = Fredoka, fontWeight = FontWeight.Bold, fontSize = 34.sp, color = nivel.tinta)
                Text(nivel.mensaje, style = MaterialTheme.typography.titleMedium, fontSize = 18.sp, color = MoradoTexto, textAlign = TextAlign.Center)
                Text(
                    "La luz de tu anillo se enciende en ${nivel.nombre.lowercase()}",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextoSuave,
                )
            }

            Text(
                nivel.consejo,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = nivel.tinta,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(nivel.fondo)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            )

            Tarjeta(relleno = 8.dp) {
                NivelPulso.entries.forEach { n -> FilaSemaforo(n, actual = n == nivel) }
            }

            // Solo para la presentación: forzar un color cuando no hay reloj conectado
            if (modoDemo && !relojEnVivo) {
                Text("Probar en demo:", style = MaterialTheme.typography.labelMedium, color = TextoSuave)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NivelPulso.entries.forEach { n ->
                        Text(
                            n.nombre,
                            style = MaterialTheme.typography.labelLarge,
                            color = n.tinta,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(n.fondo)
                                .clickable {
                                    HealthRepository.simular(n)
                                    intento++
                                }
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                        )
                    }
                }
            }

            Button(
                onClick = { intento++ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Morado, contentColor = Blanco),
            ) { Text("Medir de nuevo", style = MaterialTheme.typography.labelLarge, fontSize = 17.sp) }

            OutlinedButton(
                onClick = { navegar(Rutas.RITMO) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                border = BorderStroke(2.dp, Lila),
            ) { Text("Ver mi historial", style = MaterialTheme.typography.labelLarge, color = Morado) }
        }
    }
}

@Suppress("unused")
private val fondoNeutro = LilaSuave
