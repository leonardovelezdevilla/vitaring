package pe.vitaring.app

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import pe.vitaring.app.data.Config
import pe.vitaring.app.data.HealthRepository
import pe.vitaring.app.data.PerfilRepo
import pe.vitaring.app.data.Podometro
import pe.vitaring.app.data.Recordatorio
import pe.vitaring.app.ui.VitaRingApp
import pe.vitaring.app.ui.theme.VitaRingTheme

class MainActivity : ComponentActivity() {

    private lateinit var podometro: Podometro

    /** Recibe el pulso que envía la app del Galaxy Watch. */
    private val oyenteReloj = MessageClient.OnMessageReceivedListener { evento ->
        if (evento.path == Config.RUTA_PULSO) {
            String(evento.data).trim().toIntOrNull()?.let { HealthRepository.alRecibirDelReloj(it) }
        }
    }

    private val pedirPermisoPasos =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
            if (concedido) iniciarPodometro()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        podometro = Podometro(this)
        Recordatorio.crearCanal(this)

        // Si ya se registró, la app abre directo en Inicio.
        PerfilRepo.cargar(this)
        val tienePerfil = PerfilRepo.perfil.value != null

        setContent {
            VitaRingTheme { VitaRingApp(tienePerfil) }
        }

        val necesitaPermiso = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) !=
            PackageManager.PERMISSION_GRANTED
        if (necesitaPermiso) {
            pedirPermisoPasos.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        } else {
            iniciarPodometro()
        }
    }

    private fun iniciarPodometro() {
        HealthRepository.marcarPodometro(podometro.iniciar())
    }

    override fun onResume() {
        super.onResume()
        Wearable.getMessageClient(this).addListener(oyenteReloj)
    }

    override fun onPause() {
        Wearable.getMessageClient(this).removeListener(oyenteReloj)
        super.onPause()
    }

    override fun onDestroy() {
        podometro.detener()
        super.onDestroy()
    }
}
