package pe.vitaring.app.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsRun
import androidx.compose.material.icons.automirrored.rounded.DirectionsWalk
import androidx.compose.material.icons.rounded.EventSeat
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.vitaring.app.data.Actividad
import pe.vitaring.app.data.Perfil
import pe.vitaring.app.data.PerfilRepo
import pe.vitaring.app.data.Recordatorio
import pe.vitaring.app.ui.Encabezado
import pe.vitaring.app.ui.IconoCirculo
import pe.vitaring.app.ui.Tarjeta
import pe.vitaring.app.ui.theme.Blanco
import pe.vitaring.app.ui.theme.Borde
import pe.vitaring.app.ui.theme.LilaSuave
import pe.vitaring.app.ui.theme.Morado
import pe.vitaring.app.ui.theme.MoradoTexto
import pe.vitaring.app.ui.theme.RosaSuave
import pe.vitaring.app.ui.theme.RosaTinta
import pe.vitaring.app.ui.theme.TextoSuave

/**
 * Registro inicial (y edición) del perfil: nombre, edad, peso, talla y actividad diaria.
 * Al guardar programa el recordatorio de movimiento según la actividad elegida.
 */
@Composable
fun PantallaPerfil(onListo: () -> Unit, onVolver: (() -> Unit)?) {
    val context = LocalContext.current
    val actual = remember { PerfilRepo.perfil.value }
    val esNuevo = actual == null

    var nombre by remember { mutableStateOf(actual?.nombre ?: "") }
    var edad by remember { mutableStateOf(actual?.edad?.toString() ?: "") }
    var peso by remember { mutableStateOf(actual?.pesoKg?.let { if (it % 1f == 0f) it.toInt().toString() else it.toString() } ?: "") }
    var talla by remember { mutableStateOf(actual?.tallaCm?.toString() ?: "") }
    var actividad by remember { mutableStateOf(actual?.actividad) }
    var intentoGuardar by remember { mutableStateOf(false) }

    val edadNum = edad.trim().toIntOrNull()
    val pesoNum = peso.trim().replace(',', '.').toFloatOrNull()
    val tallaNum = talla.trim().toIntOrNull()

    val errorNombre = if (nombre.isBlank()) "Escribe tu nombre" else null
    val errorEdad = if (edadNum == null || edadNum !in 10..100) "Edad entre 10 y 100 años" else null
    val errorPeso = if (pesoNum == null || pesoNum !in 25f..250f) "Peso entre 25 y 250 kg" else null
    val errorTalla = if (tallaNum == null || tallaNum !in 100..230) "Talla entre 100 y 230 cm" else null
    val errorActividad = if (actividad == null) "Elige tu actividad diaria" else null
    val valido = listOf(errorNombre, errorEdad, errorPeso, errorTalla, errorActividad).all { it == null }

    // Se llama después de guardar (con o sin permiso de notificaciones concedido).
    val terminar: () -> Unit = {
        val horas = (actividad ?: Actividad.REGULAR).horasRecordatorio
        if (esNuevo || Recordatorio.estaActivo(context)) Recordatorio.programar(context, horas)
        onListo()
    }
    val pedirNotificaciones = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { terminar() }

    val guardar: () -> Unit = {
        intentoGuardar = true
        if (valido) {
            PerfilRepo.guardar(
                context,
                Perfil(nombre.trim(), edadNum!!, pesoNum!!, tallaNum!!, actividad!!),
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && Recordatorio.necesitaPermiso(context)) {
                pedirNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                terminar()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Encabezado(
            titulo = if (esNuevo) "Cuéntanos de ti" else "Mi perfil",
            subtitulo = "Así personalizamos tus metas y cálculos",
            onVolver = onVolver,
        )

        Tarjeta {
            Campo("Nombre", nombre, { nombre = it }, if (intentoGuardar) errorNombre else null, KeyboardType.Text)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Campo("Edad (años)", edad, { edad = it }, if (intentoGuardar) errorEdad else null, KeyboardType.Number, Modifier.weight(1f))
                Campo("Peso (kg)", peso, { peso = it }, if (intentoGuardar) errorPeso else null, KeyboardType.Decimal, Modifier.weight(1f))
            }
            Campo("Talla (cm)", talla, { talla = it }, if (intentoGuardar) errorTalla else null, KeyboardType.Number)
        }

        Text("Actividad diaria", style = MaterialTheme.typography.titleLarge, color = MoradoTexto)
        Actividad.entries.forEach { a ->
            TarjetaActividad(a, seleccionada = actividad == a) { actividad = a }
        }
        if (intentoGuardar && errorActividad != null) {
            Text(errorActividad, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = guardar,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Morado, contentColor = Blanco),
        ) {
            Text(if (esNuevo) "Guardar y continuar" else "Guardar cambios", style = MaterialTheme.typography.labelLarge, fontSize = 17.sp)
        }
        Text(
            "Te recordaremos moverte según tu actividad. Mediciones referenciales.",
            style = MaterialTheme.typography.labelMedium,
            color = TextoSuave,
        )
    }
}

@Composable
private fun Campo(
    etiqueta: String,
    valor: String,
    alCambiar: (String) -> Unit,
    error: String?,
    teclado: KeyboardType,
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        label = { Text(etiqueta) },
        singleLine = true,
        isError = error != null,
        supportingText = { if (error != null) Text(error) },
        keyboardOptions = KeyboardOptions(
            keyboardType = teclado,
            capitalization = if (teclado == KeyboardType.Text) KeyboardCapitalization.Words else KeyboardCapitalization.None,
        ),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Morado,
            focusedLabelColor = Morado,
            cursorColor = Morado,
            unfocusedBorderColor = Borde,
        ),
        modifier = modifier,
    )
}

@Composable
private fun TarjetaActividad(actividad: Actividad, seleccionada: Boolean, onClick: () -> Unit) {
    val icono = when (actividad) {
        Actividad.SEDENTARIO -> Icons.Rounded.EventSeat
        Actividad.REGULAR -> Icons.AutoMirrored.Rounded.DirectionsWalk
        Actividad.DEPORTISTA -> Icons.AutoMirrored.Rounded.DirectionsRun
    }
    val forma = RoundedCornerShape(20.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(forma)
            .background(if (seleccionada) LilaSuave else Blanco)
            .border(if (seleccionada) 2.dp else 1.dp, if (seleccionada) Morado else Borde, forma)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (seleccionada) IconoCirculo(icono, Morado, Blanco, 40.dp) else IconoCirculo(icono, RosaSuave, RosaTinta, 40.dp)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(actividad.titulo, style = MaterialTheme.typography.titleMedium, color = MoradoTexto)
            Text(actividad.descripcion, style = MaterialTheme.typography.labelMedium, color = TextoSuave)
            Text(
                "Meta: ${actividad.metaPasos / 1000} mil pasos · aviso cada ${actividad.horasRecordatorio} h",
                style = MaterialTheme.typography.labelMedium,
                color = Morado,
            )
        }
        if (seleccionada) Icon(Icons.Rounded.CheckCircle, contentDescription = "Seleccionada", tint = Morado)
    }
}
