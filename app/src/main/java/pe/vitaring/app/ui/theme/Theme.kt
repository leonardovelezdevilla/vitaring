package pe.vitaring.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import pe.vitaring.app.R

// ---------- Paleta oficial de VitaRing ----------
val Morado = Color(0xFF4B2E83)      // Morado oscuro (predominante)
val Rosa = Color(0xFFF7D5EB)        // Rosa claro (predominante)
val Lila = Color(0xFFB89BD9)        // Lila (secundario)

// ---------- Tonos derivados para la interfaz ----------
val MoradoTexto = Color(0xFF2E1A5C)
val MoradoClaro = Color(0xFF6A4FA3)
val LilaSuave = Color(0xFFEFE6FB)
val LilaTexto = Color(0xFFE3D6F7)
val RosaSuave = Color(0xFFFCEAF2)
val RosaBrillo = Color(0xFFF4A7C9)
val RosaAcento = Color(0xFFD9579A)
val RosaTinta = Color(0xFFB83C7A)
val Fondo = Color(0xFFFBF7FD)
val Borde = Color(0xFFEDE3F8)
val TextoSuave = Color(0xFF6E5C93)
val Inactivo = Color(0xFF7A6A9C)
val VerdeOk = Color(0xFF1A7A50)
val Blanco = Color.White

// ---------- Tipografías (archivos en res/font) ----------
val Fredoka = FontFamily(
    Font(R.font.fredoka, FontWeight.Medium),
    Font(R.font.fredoka, FontWeight.SemiBold),
    Font(R.font.fredoka, FontWeight.Bold),
)

val Nunito = FontFamily(
    Font(R.font.nunito, FontWeight.Normal),
    Font(R.font.nunito, FontWeight.SemiBold),
    Font(R.font.nunito, FontWeight.Bold),
    Font(R.font.nunito, FontWeight.ExtraBold),
)

val PatrickHand = FontFamily(Font(R.font.patrick_hand))

private val Tipografia = Typography(
    displayLarge = TextStyle(fontFamily = Fredoka, fontWeight = FontWeight.SemiBold, fontSize = 64.sp, lineHeight = 66.sp),
    headlineMedium = TextStyle(fontFamily = Fredoka, fontWeight = FontWeight.SemiBold, fontSize = 26.sp, lineHeight = 32.sp),
    headlineSmall = TextStyle(fontFamily = Fredoka, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 30.sp),
    titleLarge = TextStyle(fontFamily = Fredoka, fontWeight = FontWeight.SemiBold, fontSize = 19.sp, lineHeight = 24.sp),
    titleMedium = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.sp, lineHeight = 18.sp),
    labelSmall = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 11.sp, lineHeight = 14.sp),
)

/** Estilo para números grandes (pulso, pasos, etc.). */
fun estiloNumero(tamano: Int, color: Color = MoradoTexto) = TextStyle(
    fontFamily = Fredoka,
    fontWeight = FontWeight.SemiBold,
    fontSize = tamano.sp,
    lineHeight = (tamano + 2).sp,
    color = color,
)

@Composable
fun VitaRingTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Morado,
            onPrimary = Blanco,
            secondary = Lila,
            onSecondary = MoradoTexto,
            tertiary = RosaAcento,
            background = Fondo,
            onBackground = MoradoTexto,
            surface = Blanco,
            onSurface = MoradoTexto,
            surfaceVariant = LilaSuave,
            onSurfaceVariant = TextoSuave,
            outline = Borde,
        ),
        typography = Tipografia,
        content = content,
    )
}
