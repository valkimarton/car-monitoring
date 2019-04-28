package hu.bme.aut.android.carmonitoringapp.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.TextView
import com.google.android.gms.maps.model.LatLng
import hu.bme.aut.android.carmonitoringapp.MeasureApplication
import hu.bme.aut.android.carmonitoringapp.database.MyDatabase
import hu.bme.aut.android.carmonitoringapp.database.dao.MeasureDao
import hu.bme.aut.android.carmonitoringapp.model.Measure

class AccEventListener(
    val context: Context,
    val accelerationXView: TextView,
    val accelerationYView: TextView,
    val accelerationZView: TextView,
    val myLatLong: MyLatLong): SensorEventListener {

    private val sensorManager: SensorManager
    private var db: MyDatabase? = null
    private var measureDao: MeasureDao? = null

    private var startTime: Double
    private val MIN_SAMPLING_TIME: Double = 0.05   // Sec

    private var previousTime: Double = -MIN_SAMPLING_TIME  // To record the first measure point

    init {
        // registering listener to the ACC sensor
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // Database connection handler
        db = MeasureApplication.db
        measureDao = db?.measureDao()

        startTime = ( System.currentTimeMillis() / 1000 ).toDouble()
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        println("!!! NEW ACCURACY : ${accuracy} ")
    }

    override fun onSensorChanged(event: SensorEvent?) {

        val time: Double = ( System.currentTimeMillis() / 1000.0 ) - startTime

        if (time >= previousTime + MIN_SAMPLING_TIME){
            event?.let {
                val accXstring: String = String.format("%.2f", event.values[0])
                val accYstring: String = String.format("%.2f", event.values[1])
                val accZstring: String = String.format("%.2f", event.values[2])

                accelerationXView.setText(accXstring)
                accelerationYView.setText(accYstring)
                accelerationZView.setText(accZstring)

                measureDao?.insertMeasure(
                    Measure(
                        myLatLong.latitude,
                        myLatLong.longitude,
                        event.values[0].toDouble(),
                        event.values[1].toDouble(),
                        event.values[2].toDouble(),
                        time
                    )
                )

                previousTime = time
            }
        }


    }

    fun register() {
        startTime = ( System.currentTimeMillis() / 1000 ).toDouble()
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