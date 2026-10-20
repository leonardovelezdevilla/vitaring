package pe.vitaring.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsWalk
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.vitaring.app.ui.IconoCirculo
import pe.vitaring.app.ui.theme.Blanco
import pe.vitaring.app.ui.theme.Fredoka
import pe.vitaring.app.ui.theme.Lila
import pe.vitaring.app.ui.theme.LilaSuave
import pe.vitaring.app.ui.theme.Morado
import pe.vitaring.app.ui.theme.MoradoTexto
import pe.vitaring.app.ui.theme.PatrickHand
import pe.vitaring.app.ui.theme.Rosa
import pe.vitaring.app.ui.theme.RosaAcento
import pe.vitaring.app.ui.theme.RosaBrillo
import pe.vitaring.app.ui.theme.RosaSuave
import pe.vitaring.app.ui.theme.RosaTinta
import androidx.compose.foundation.BorderStroke

@Composable
fun PantallaBienvenida(onComenzar: () -> Unit, onVincular: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RosaSuave)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        // Logo: anillo con corazón
        Box(
            modifier = Modifier.size(168.dp).clip(CircleShape).background(Rosa).border(6.dp, Lila, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier.size(108.dp).clip(CircleShape).background(RosaSuave).border(14.dp, Morado, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.Favorite, contentDescription = null, tint = RosaAcento, modifier = Modifier.size(40.dp))
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("VITARING", fontFamily = Fredoka, fontWeight = FontWeight.Bold, fontSize = 52.sp, color = Morado, letterSpacing = 1.sp)
            Text("Anillo inteligente", fontFamily = PatrickHand, fontSize = 26.sp, color = Morado)
        }

        Text(
            text = "“Tu salud y actividad física en un solo lugar”",
            fontFamily = PatrickHand,
            fontSize = 21.sp,
            lineHeight = 26.sp,
            color = MoradoTexto,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(RosaBrillo)
                .padding(horizontal = 22.dp, vertical = 10.dp),
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Beneficio(Icons.Rounded.Favorite, "Pulso, SpO₂ y estrés en tiempo real", morado = true)
            Beneficio(Icons.AutoMirrored.Rounded.DirectionsWalk, "Pasos, calorías y entrenamientos", morado = false)
            Beneficio(Icons.Rounded.Bedtime, "Sueño y temperatura mientras descansas", morado = true)
        }

        Spacer(Modifier.height(4.dp))

        Button(
            onClick = onComenzar,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Morado, contentColor = Blanco),
        ) {
            Text("Comenzar", style = MaterialTheme.typography.labelLarge, fontSize = 17.sp)
        }
        OutlinedButton(
            onClick = onVincular,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            border = BorderStroke(2.dp, Lila),
        ) {
            Text("Vincular mi reloj o anillo", style = MaterialTheme.typography.labelLarge, color = Morado)
        }
    }
}

@Composable
private fun Beneficio(icono: ImageVector, texto: String, morado: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Blanco)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (morado) IconoCirculo(icono, LilaSuave, Morado, 36.dp) else IconoCirculo(icono, RosaSuave, RosaTinta, 36.dp)
        Text(texto, style = MaterialTheme.typography.titleMedium, color = MoradoTexto)
    }
}
