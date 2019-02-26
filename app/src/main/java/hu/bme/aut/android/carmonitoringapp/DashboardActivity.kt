package hu.bme.aut.android.carmonitoringapp

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.TextView

import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerationSensor: Sensor? = null

    private lateinit var accelerationXView: TextView //= findViewById<TextView>(R.id.acceleration_x_text)
    private lateinit var accelerationYView: TextView //= findViewById<TextView>(R.id.acceleration_y_text)
    private lateinit var accelerationZView: TextView //= findViewById<TextView>(R.id.acceleration_z_text)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        accelerationXView = findViewById(R.id.acceleration_x_text)
        accelerationYView = findViewById(R.id.acceleration_y_text)
        accelerationZView = findViewById(R.id.acceleration_z_text)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )

        /*for (i: Int in 1..10) {
            Handler().postDelayed(
                {
                    accelerationXView.setText((i).toString())
                    accelerationYView.setText((i*2).toString())
                    accelerationZView.setText((i*3).toString())
                },
                i.toLong()*1000
            )

        }*/
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { event -> accelerationXView.setText( String.format("%.2f", event.values[0]) )
                accelerationYView.setText( String.format("%.2f", event.values[1]) )
                accelerationZView.setText( String.format("%.2f", event.values[2]) ) }
        //accelerationXView.setText( String.format("%.2f", event!!.values[0]) )
        //accelerationYView.setText( String.format("%.2f", event!!.values[1]) )
        //accelerationZView.setText( String.format("%.2f", event!!.values[2]) )
    }

}
