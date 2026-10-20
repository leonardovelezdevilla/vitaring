package pe.vitaring.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsWalk
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import pe.vitaring.app.ui.screens.PantallaActividad
import pe.vitaring.app.ui.screens.PantallaAnillo
import pe.vitaring.app.ui.screens.PantallaBienestar
import pe.vitaring.app.ui.screens.PantallaBienvenida
import pe.vitaring.app.ui.screens.PantallaInicio
import pe.vitaring.app.ui.screens.PantallaResultado
import pe.vitaring.app.ui.screens.PantallaRitmo
import pe.vitaring.app.ui.theme.Blanco
import pe.vitaring.app.ui.theme.Borde
import pe.vitaring.app.ui.theme.Fondo
import pe.vitaring.app.ui.theme.Inactivo
import pe.vitaring.app.ui.theme.LilaSuave
import pe.vitaring.app.ui.theme.Morado

object Rutas {
    const val BIENVENIDA = "bienvenida"
    const val INICIO = "inicio"
    const val RITMO = "ritmo"
    const val ACTIVIDAD = "actividad"
    const val BIENESTAR = "bienestar"
    const val ANILLO = "anillo"
    const val RESULTADO = "resultado"
}

private data class Pestana(val ruta: String, val titulo: String, val icono: ImageVector)

private val pestanas = listOf(
    Pestana(Rutas.INICIO, "Inicio", Icons.Rounded.Home),
    Pestana(Rutas.RITMO, "Corazón", Icons.Rounded.Favorite),
    Pestana(Rutas.ACTIVIDAD, "Actividad", Icons.AutoMirrored.Rounded.DirectionsWalk),
    Pestana(Rutas.BIENESTAR, "Bienestar", Icons.Rounded.Bedtime),
    Pestana(Rutas.ANILLO, "Anillo", Icons.Rounded.RadioButtonUnchecked),
)

@Composable
fun VitaRingApp() {
    val nav = rememberNavController()
    val entrada by nav.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route
    val conBarra = pestanas.any { it.ruta == rutaActual }

    val navegar: (String) -> Unit = { ruta ->
        if (pestanas.any { it.ruta == ruta }) {
            nav.navigate(ruta) {
                popUpTo(Rutas.INICIO) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        } else {
            nav.navigate(ruta)
        }
    }
    val volver: () -> Unit = { nav.popBackStack() }

    Scaffold(
        containerColor = Fondo,
        bottomBar = {
            if (conBarra) {
                Column {
                    HorizontalDivider(color = Borde)
                    NavigationBar(containerColor = Blanco, tonalElevation = 0.dp) {
                        pestanas.forEach { p ->
                            NavigationBarItem(
                                selected = rutaActual == p.ruta,
                                onClick = { navegar(p.ruta) },
                                icon = { Icon(p.icono, contentDescription = null) },
                                label = { Text(p.titulo) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Morado,
                                    selectedTextColor = Morado,
                                    indicatorColor = LilaSuave,
                                    unselectedIconColor = Inactivo,
                                    unselectedTextColor = Inactivo,
                                ),
                            )
                        }
                    }
                }
            }
        },
    ) { relleno ->
        NavHost(
            navController = nav,
            startDestination = Rutas.BIENVENIDA,
            modifier = Modifier.padding(relleno),
        ) {
            composable(Rutas.BIENVENIDA) {
                PantallaBienvenida(
                    onComenzar = {
                        nav.navigate(Rutas.INICIO) { popUpTo(Rutas.BIENVENIDA) { inclusive = true } }
                    },
                    onVincular = {
                        nav.navigate(Rutas.INICIO) { popUpTo(Rutas.BIENVENIDA) { inclusive = true } }
                        navegar(Rutas.ANILLO)
                    },
                )
            }
            composable(Rutas.INICIO) { PantallaInicio(navegar) }
            composable(Rutas.RITMO) { PantallaRitmo(navegar) }
            composable(Rutas.ACTIVIDAD) { PantallaActividad() }
            composable(Rutas.BIENESTAR) { PantallaBienestar() }
            composable(Rutas.ANILLO) { PantallaAnillo(navegar) }
            composable(Rutas.RESULTADO) { PantallaResultado(volver, navegar) }
        }
    }
}
