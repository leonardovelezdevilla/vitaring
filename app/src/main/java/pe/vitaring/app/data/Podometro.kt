package pe.vitaring.app.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import java.time.LocalDate

/**
 * Podómetro real usando el sensor de pasos del celular.
 * El sensor entrega pasos acumulados desde que se encendió el teléfono,
 * así que guardamos el valor del inicio del día y restamos.
 */
class Podometro(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private val prefs = context.getSharedPreferences("podometro", Context.MODE_PRIVATE)

    /** Devuelve true si el celular tiene sensor de pasos y se pudo iniciar. */
    fun iniciar(): Boolean {
        val s = sensor ?: return false
        return sensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_UI)
    }

    fun detener() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val total = event.values[0].toInt()
        val hoy = LocalDate.now().toString()
        var base = prefs.getInt("base", -1)
        val fecha = prefs.getString("fecha", null)

        // Nuevo día, primera vez o el teléfono se reinició: reiniciamos la base.
        if (fecha != hoy || base < 0 || base > total) {
            base = total
            prefs.edit().putString("fecha", hoy).putInt("base", base).apply()
        }
        HealthRepository.actualizarPasosReales(total - base)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
