package pe.vitaring.app.data

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import pe.vitaring.app.MainActivity
import pe.vitaring.app.R
import java.util.concurrent.TimeUnit

/**
 * Recordatorio para hacer movimiento físico.
 * Usa WorkManager: funciona aunque la app esté cerrada.
 */
object Recordatorio {
    private const val CANAL = "movimiento"
    private const val TRABAJO = "recordatorio_movimiento"
    private const val PREFS = "recordatorio"
    private const val ID_NOTIFICACION = 1001

    fun necesitaPermiso(context: Context): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED

    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(CANAL, "Recordatorios de movimiento", NotificationManager.IMPORTANCE_DEFAULT)
            canal.description = "Te avisa cuando llevas mucho tiempo sin moverte"
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
        }
    }

    /** Programa el recordatorio cada [horas] horas. */
    fun programar(context: Context, horas: Int) {
        val pedido = PeriodicWorkRequestBuilder<RecordatorioWorker>(horas.toLong(), TimeUnit.HOURS)
            .setInitialDelay(horas.toLong(), TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(TRABAJO, ExistingPeriodicWorkPolicy.UPDATE, pedido)
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean("activo", true).putInt("horas", horas).apply()
    }

    fun cancelar(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(TRABAJO)
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean("activo", false).apply()
    }

    fun estaActivo(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean("activo", false)

    /** Muestra la notificación (también sirve para probarla en la presentación). */
    @SuppressLint("MissingPermission")
    fun mostrar(context: Context) {
        if (necesitaPermiso(context)) return
        crearCanal(context)
        val abrirApp = PendingIntent.getActivity(
            context, 0, Intent(context, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE,
        )
        val nombre = PerfilRepo.perfil.value?.nombre?.let { ", $it" } ?: ""
        val notificacion = NotificationCompat.Builder(context, CANAL)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle("¡Hora de moverte$nombre!")
            .setContentText("Levántate y camina unos 5 minutos.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Llevas un buen rato sin actividad. Levántate, estira y camina unos 5 minutos: suma pasos a tu meta diaria."),
            )
            .setColor(0xFF4B2E83.toInt())
            .setContentIntent(abrirApp)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(ID_NOTIFICACION, notificacion)
    }
}

class RecordatorioWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        PerfilRepo.cargar(applicationContext)
        Recordatorio.mostrar(applicationContext)
        return Result.success()
    }
}
