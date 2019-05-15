package hu.bme.aut.android.carmonitoringapp

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.SeekBar
import android.widget.TextView

import kotlinx.android.synthetic.main.activity_dashboard.*
import android.R.color
import android.animation.ObjectAnimator
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Looper
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import hu.bme.aut.android.carmonitoringapp.model.Measure
import hu.bme.aut.android.carmonitoringapp.sensor.MyLatLong


class DashboardActivity : AppCompatActivity(), SensorEventListener{

    private lateinit var sensorManager: SensorManager
    private var accelerationSensor: Sensor? = null

    private lateinit var accelerationXView: TextView //= findViewById<TextView>(R.id.acceleration_x_text)
    private lateinit var accelerationYView: TextView //= findViewById<TextView>(R.id.acceleration_y_text)
    private lateinit var accelerationZView: TextView //= findViewById<TextView>(R.id.acceleration_z_text)
    private lateinit var velocityDisplayView: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var ellipse: View
    private lateinit var accVector: View
    private lateinit var batteryText: TextView

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest : LocationRequest
    lateinit var locationCallback: LocationCallback
    val myLatLong: MyLatLong = MyLatLong(0.0,0.0)
    var previousTime: Double = 0.0
    var startTime: Double = ( System.currentTimeMillis() / 1000 ).toDouble()
    var oldspeed: Double = 0.0

    val GPS_REQUEST_CODE = 1000


    var gps_longitude: Double = 0.0
    var gps_latitude: Double = 0.0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        setSupportActionBar(toolbar)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        accelerationXView = findViewById(R.id.acceleration_x_text)
        accelerationYView = findViewById(R.id.acceleration_y_text)
        accelerationZView = findViewById(R.id.acceleration_z_text)

        velocityDisplayView = findViewById(R.id.text_view_current_velocity)
        seekBar = findViewById(R.id.seekBar1)
        ellipse = findViewById(R.id.ellipse)
        accVector = findViewById(R.id.vector_2)
        batteryText = findViewById(R.id.text_view_battery_percentage)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
            SensorManager.SENSOR_DELAY_NORMAL
        )


        buildLocationRequest()
        buildLocationCallback()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf( android.Manifest.permission.ACCESS_FINE_LOCATION), GPS_REQUEST_CODE)
        } else {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        }

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

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
               // velocityDisplayView.text = "$i"
                

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Do something
            }
        })

    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        println( "!!! NEW ACCURACY : ${accuracy} ")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { event ->
            accelerationXView.setText( String.format("%.2f", event.values[0]) )
            accelerationYView.setText( String.format("%.2f", event.values[1]) )
            accelerationZView.setText( String.format("%.2f", event.values[2]) ) }
        //accelerationXView.setText( String.format("%.2f", event!!.values[0]) )
        //accelerationYView.setText( String.format("%.2f", event!!.values[1]) )
        //accelerationZView.setText( String.format("%.2f", event!!.values[2]) )






    }
    private fun buildLocationCallback() {
        locationCallback = object: LocationCallback(){
            override fun onLocationResult(p0: LocationResult?) {

                var location = p0!!.locations.get(p0.locations.size-1)

                gps_longitude = myLatLong.longitude
                gps_latitude = myLatLong.latitude

                myLatLong.latitude = location.latitude
                myLatLong.longitude = location.longitude

                val currentTime = ( System.currentTimeMillis() / 1000 ).toDouble()
                val timediff = currentTime - previousTime

                val speed = calculateSpeed(gps_longitude, gps_latitude, timediff)*3.6


                previousTime = currentTime

                val rotateStart = -210f + 4 * oldspeed.toInt()
                val rotateEnd = -210f + 4 * speed.toInt()


                val imageViewObjectAnimator = ObjectAnimator.ofFloat(
                    ellipse,
                    "rotation", rotateStart, rotateEnd
                )

                imageViewObjectAnimator.setDuration(1000); // miliseconds
                imageViewObjectAnimator.start();


                batteryText.text = speed.toString()
                velocityDisplayView.text = speed.toInt().toString()

                oldspeed = speed

            }
        }
    }

    private fun buildLocationRequest() {

        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 160
        locationRequest.fastestInterval = 80
        locationRequest.smallestDisplacement = 0.2f

    }

    fun calculateSpeed(longitude: Double, latitude: Double, timediff: Double): Double {
        val startLocation = Location("start")
        startLocation.setLatitude(latitude)
        startLocation.setLongitude(longitude)

        val endLocation = Location("end")
        endLocation.setLatitude(myLatLong.latitude)
        endLocation.setLongitude(myLatLong.longitude)

        val distanceInMeters: Double = startLocation.distanceTo(endLocation).toDouble()

        return distanceInMeters/timediff
    }

}
