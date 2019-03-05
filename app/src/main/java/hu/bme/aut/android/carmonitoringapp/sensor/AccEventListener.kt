package hu.bme.aut.android.carmonitoringapp.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.TextView
import hu.bme.aut.android.carmonitoringapp.MeasureApplication
import hu.bme.aut.android.carmonitoringapp.database.MeasureDbLoader
import hu.bme.aut.android.carmonitoringapp.model.Measure

class AccEventListener(
    val context: Context,
    val accelerationXView: TextView,
    val accelerationYView: TextView,
    val accelerationZView: TextView): SensorEventListener {

    private val sensorManager: SensorManager
    private val dbLoader: MeasureDbLoader

    private val startTime: Double

    init {
        // registering listener to the ACC sensor
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // Database connection handler
        dbLoader = MeasureApplication.measureDbLoader

        startTime = ( System.currentTimeMillis() / 1000 ).toDouble()
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        println("!!! NEW ACCURACY : ${accuracy} ")
    }

    override fun onSensorChanged(event: SensorEvent?) {

        val time: Double = ( System.currentTimeMillis() / 1000.0 ).toDouble() - startTime

        event?.let {
            val accXstring: String = String.format("%.2f", event.values[0])
            val accYstring: String = String.format("%.2f", event.values[1])
            val accZstring: String = String.format("%.2f", event.values[2])

            accelerationXView.setText(accXstring)
            accelerationYView.setText(accYstring)
            accelerationZView.setText(accZstring)

            this.dbLoader.createMeasure(
                Measure(
                    0.0,
                    0.0,
                    event.values[0].toDouble(),
                    event.values[1].toDouble(),
                    event.values[2].toDouble(),
                    time
                )
            )
        }

    }

    fun register() {
        this.sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    fun unregister() {
        this.sensorManager.unregisterListener(this)
    }

}