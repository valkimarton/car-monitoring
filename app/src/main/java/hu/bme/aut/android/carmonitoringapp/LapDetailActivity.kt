package hu.bme.aut.android.carmonitoringapp

import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.widget.Button
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import hu.bme.aut.android.carmonitoringapp.database.MyDatabase
import hu.bme.aut.android.carmonitoringapp.database.dao.LapDao
import hu.bme.aut.android.carmonitoringapp.model.Lap
import hu.bme.aut.android.carmonitoringapp.model.Measure

import kotlinx.android.synthetic.main.activity_lap_detail.*
import java.util.*
import kotlin.collections.ArrayList

class LapDetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private var db: MyDatabase? = null
    private var lapDao: LapDao? = null

    private lateinit var mMap: GoogleMap

    private lateinit var lap: Lap
    private var noOfPoints: Int = 0

    private var minSpeed: Double = 0.0
    private var maxSpeed: Double = 0.0
    private var minAccX: Double = 0.0
    private var maxAccX: Double = 0.0
    private var minAccY: Double = 0.0
    private var maxAccY: Double = 0.0
    private var minAccZ: Double = 0.0
    private var maxAccZ: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lap_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initDatabase()

        val lapName: String = intent.extras.getString("name")
        lap = lapDao?.getLapByName(lapName) ?: Lap("empty_lap", Date(), ArrayList<Measure>())
        noOfPoints = lap.measures.size

        getViewsAndInitListeners()

        finMinAndMaxValues()



        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        drawPathAndSetCameraPosition()
    }

    fun initDatabase(){
        // Database connection handler
        db = MeasureApplication.db
        lapDao = db?.lapDao()
    }

    fun getViewsAndInitListeners() {
        val speedRadioButton: Button = findViewById(R.id.radiobutton_speed)
        val accXRadioButton: Button = findViewById(R.id.radiobutton_accx)
        val accYRadioButton: Button = findViewById(R.id.radiobutton_accy)
        val accZRadioButton: Button = findViewById(R.id.radiobutton_accz)

        speedRadioButton.setOnClickListener {
            colorPathBySpeed()
        }

        accXRadioButton.setOnClickListener {
            colorPathByAccX()
        }

        accYRadioButton.setOnClickListener {
            colorPathByAccY()
        }

        accZRadioButton.setOnClickListener {
            colorPathByAccZ()
        }
    }

    fun colorPathBySpeed() {
        mMap.clear()
        val measures: List<Measure> = lap.measures
        var previousMeasure: Measure = measures.get(0)

        for (i in 1 until noOfPoints) {
            val measure: Measure = measures.get(i)
            val speed: Double = calculateSpeed(measure, previousMeasure)
            val color: Color = getColorFromMinAndMax(speed, minSpeed, maxSpeed)

            val options: PolylineOptions = PolylineOptions().width(5f).color(color.toArgb()).geodesic(true)
            options.add(LatLng(previousMeasure.latitude, previousMeasure.longitude))
            options.add(LatLng(measure.latitude, measure.longitude))
            mMap.addPolyline(options)


            previousMeasure = measure
        }
    }

    fun colorPathByAccX() {
        mMap.clear()
        val measures: List<Measure> = lap.measures
        var previousMeasure: Measure = measures.get(0)

        for (i in 1 until noOfPoints) {
            val measure: Measure = measures.get(i)
            val color: Color = getColorFromMinAndMax(measure.accX, minAccX, maxAccX)

            val options: PolylineOptions = PolylineOptions().width(5f).color(color.toArgb()).geodesic(true)
            options.add(LatLng(previousMeasure.latitude, previousMeasure.longitude))
            options.add(LatLng(measure.latitude, measure.longitude))
            mMap.addPolyline(options)


            previousMeasure = measure
        }
    }

    fun colorPathByAccY() {
        mMap.clear()
        val measures: List<Measure> = lap.measures
        var previousMeasure: Measure = measures.get(0)

        for (i in 1 until noOfPoints) {
            val measure: Measure = measures.get(i)
            val color: Color = getColorFromMinAndMax(measure.accY, minAccY, maxAccY)

            val options: PolylineOptions = PolylineOptions().width(5f).color(color.toArgb()).geodesic(true)
            options.add(LatLng(previousMeasure.latitude, previousMeasure.longitude))
            options.add(LatLng(measure.latitude, measure.longitude))
            mMap.addPolyline(options)


            previousMeasure = measure
        }
    }

    fun colorPathByAccZ() {
        mMap.clear()
        val measures: List<Measure> = lap.measures
        var previousMeasure: Measure = measures.get(0)

        for (i in 1 until noOfPoints) {
            val measure: Measure = measures.get(i)
            val color: Color = getColorFromMinAndMax(measure.accZ, minAccZ, maxAccZ)

            val options: PolylineOptions = PolylineOptions().width(5f).color(color.toArgb()).geodesic(true)
            options.add(LatLng(previousMeasure.latitude, previousMeasure.longitude))
            options.add(LatLng(measure.latitude, measure.longitude))
            mMap.addPolyline(options)


            previousMeasure = measure
        }
    }

    fun getColorFromMinAndMax(current: Double, min: Double, max: Double): Color {
        var red: Double = 0.0
        var green: Double = 0.0

        var proportion = (current - min) / (max - min)

        if (proportion < 0.5) {
            red = proportion / 0.5
            green = 1.0
        } else {
            red = 1.0
            green = 1f - (proportion-0.5) / 0.5
        }

        return Color.valueOf(red.toFloat(), green.toFloat(), 0f)
    }

    fun finMinAndMaxValues() {
        val measures: List<Measure> = lap.measures
        var previousMeasure = measures.get(0)

        for (i in 1 until noOfPoints){
            val measure = measures.get(i)

            val accX: Double = (measure.accX + previousMeasure.accX)/2
            val accY: Double = (measure.accY + previousMeasure.accY)/2
            val accZ: Double = (measure.accZ + previousMeasure.accZ)/2
            val speed: Double = calculateSpeed(measure, previousMeasure)

            if (accX > maxAccX)
                maxAccX = accX
            if (accX < minAccX)
                minAccX = accX
            if (accY > maxAccY)
                maxAccY = accY
            if (accY < minAccY)
                minAccY = accY
            if (accZ > maxAccZ)
                maxAccZ = accZ
            if (accZ < minAccZ)
                minAccZ = accZ

            if (speed > maxSpeed)
                maxSpeed = speed

            previousMeasure = measure
        }
    }

    fun calculateSpeed(measure: Measure, previousMeasure: Measure): Double {
        val startLocation = Location("start")
        startLocation.setLatitude(measure.latitude)
        startLocation.setLongitude(measure.longitude)

        val endLocation = Location("end")
        endLocation.setLatitude(previousMeasure.latitude)
        endLocation.setLongitude(previousMeasure.longitude)

        val distanceInMeters: Double = startLocation.distanceTo(endLocation).toDouble()

        val timeInSeconds: Double = measure.time - previousMeasure.time

        return distanceInMeters/timeInSeconds
    }

    fun drawPathAndSetCameraPosition() {

        val options: PolylineOptions = PolylineOptions().width(5f).color(Color.BLUE).geodesic(true)

        val measures: List<Measure> = lap.measures
        val firstMeasure: Measure = measures.get(0)

        var minLat: Double = firstMeasure.latitude
        var maxLat: Double = firstMeasure.latitude
        var minLong: Double = firstMeasure.longitude
        var maxLong: Double = firstMeasure.longitude

        for (i in 0 until noOfPoints){
            val measure = measures.get(i)
            options.add(LatLng(measure.latitude, measure.longitude))

            if (measure.latitude < minLat)
                minLat = measure.latitude
            if (measure.latitude > maxLat)
                maxLat = measure.latitude
            if (measure.longitude < minLong)
                minLong = measure.longitude
            if (measure.longitude > maxLong)
                maxLong = measure.longitude
        }

        var line: Polyline = mMap.addPolyline(options)

        val minLatLong: LatLng = LatLng(minLat, minLong)
        val maxLatLong: LatLng = LatLng(maxLat, maxLong)

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(LatLngBounds(minLatLong, maxLatLong), 140))

    }

}
