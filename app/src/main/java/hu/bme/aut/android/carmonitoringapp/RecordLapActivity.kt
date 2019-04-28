package hu.bme.aut.android.carmonitoringapp

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import hu.bme.aut.android.carmonitoringapp.database.DbConstants
import hu.bme.aut.android.carmonitoringapp.database.MyDatabase
import hu.bme.aut.android.carmonitoringapp.database.dao.LapDao
import hu.bme.aut.android.carmonitoringapp.database.dao.MeasureDao
import hu.bme.aut.android.carmonitoringapp.fragments.YesNoDialog
import hu.bme.aut.android.carmonitoringapp.model.Lap
import hu.bme.aut.android.carmonitoringapp.model.Measure
import hu.bme.aut.android.carmonitoringapp.sensor.AccEventListener
import hu.bme.aut.android.carmonitoringapp.sensor.MyLatLong

import kotlinx.android.synthetic.main.activity_record_lap.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class RecordLapActivity : AppCompatActivity(), YesNoDialog.OnDialogSaveLap, OnMapReadyCallback {

    // Database stuffs
    private var db: MyDatabase? = null
    private var measureDao: MeasureDao? = null
    private var lapDao: LapDao? = null

    // GPS
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest : LocationRequest
    lateinit var locationCallback: LocationCallback
    val myLatLong: MyLatLong = MyLatLong(0.0,0.0)

    val GPS_REQUEST_CODE = 1000

    // map
    private lateinit var mMap: GoogleMap
    private var locationList: ArrayList<LatLng> = ArrayList<LatLng>()
    private lateinit var line: Polyline

    // Sensor stuffs
    private lateinit var accEventListener: AccEventListener

    // Views
    private lateinit var accelerationXView: TextView
    private lateinit var accelerationYView: TextView
    private lateinit var accelerationZView: TextView

    private lateinit var startButton: Button
    private lateinit var stopButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_lap)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initDatabase()

        // location updates
        buildLocationRequest()
        buildLocationCallback()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        getViewsAndInitListeners()

        accEventListener = AccEventListener(this, accelerationXView, accelerationYView, accelerationZView, myLatLong)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //Check permission
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION )){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), GPS_REQUEST_CODE)
        }


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add polylines and polygons to the map. This section shows just
        // a single polyline. Read the rest of the tutorial to learn more.
        val options: PolylineOptions = PolylineOptions().width(5f).color(Color.BLUE).geodesic(true)
        line = mMap.addPolyline(options)

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(47.4813, 19.0555), 18.0f))  // BME
    }


    override fun onSaveLap(lapName: String) {
        val lapDate: Date = Date()
        val measures: List<Measure> = measureDao?.getMeasures() ?: ArrayList<Measure>()

        // process Location data
        fixValuesWithZeroLocation(measures)
        interpolateLocationEqualLocationPointsByTime(measures)

        val newLap: Lap = Lap(
            name = lapName,
            date = lapDate,
            measures = measures
        )
        lapDao?.insert(newLap)

        // Deleting the measurement points from the 'Measure' table
        measureDao?.deleteAllMeasures()

        println("LAP SAVED")
    }

    override fun onDismissLap() {
        measureDao?.deleteAllMeasures()

        println("LAP DISMISSED")
    }

    fun fixValuesWithZeroLocation(measures: List<Measure>) {
        // Fix (0,0) locations at the beginning of the list
        var i: Int = 0
        while (measures.get(i).latitude == 0.0){
            i++
        }

        val indexOfFirstValid: Int = i
        val firstValidLatitude: Double = measures.get(indexOfFirstValid).latitude
        val firstValidLongitude: Double = measures.get(indexOfFirstValid).longitude

        for (j in 0 until indexOfFirstValid){
            measures.get(j).latitude = firstValidLatitude
            measures.get(j).longitude = firstValidLongitude
        }
    }

    fun interpolateLocationEqualLocationPointsByTime(measures: List<Measure>) {
        var currentIndex: Int = 0
        var frontIndex: Int = 0
        var backIndex: Int = 0

        while (frontIndex < measures.size) {
            while (measures.get(frontIndex).latitude == measures.get(backIndex).latitude) {
                if (frontIndex + 1 >= measures.size)
                    return
                frontIndex++
            }

            while (currentIndex < frontIndex) {
                var current: Measure = measures.get(currentIndex)
                var back: Measure = measures.get(backIndex)
                var front: Measure = measures.get(frontIndex)

                val timeProportion: Double = (current.time - back.time) / (front.time - back.time)
                current.latitude = back.latitude + timeProportion * (front.latitude - back.latitude)
                current.longitude = back.longitude + timeProportion * (front.longitude - back.longitude)

                currentIndex++
            }
            backIndex = frontIndex
        }
    }

    fun initDatabase(){
        // Database connection handler
        db = MeasureApplication.db
        measureDao = db?.measureDao()
        lapDao = db?.lapDao()
    }

    private fun buildLocationCallback() {
        locationCallback = object: LocationCallback(){
            override fun onLocationResult(p0: LocationResult?) {

                var location = p0!!.locations.get(p0.locations.size-1)

                myLatLong.latitude = location.latitude
                myLatLong.longitude = location.longitude

                locationList.add(LatLng(location.latitude, location.longitude))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), mMap.cameraPosition.zoom), 1000, null)

                redrawLine()
                mMap.addMarker(MarkerOptions().position(LatLng(location.latitude, location.longitude)).title("Marker"))

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

    private fun redrawLine() {

        mMap.clear()  //clears all Markers and Polylines

        val options = PolylineOptions().width(5f).color(Color.BLUE).geodesic(true)
        for (i in 0 until locationList.size) {
            val point = locationList.get(i)
            options.add(point)
        }
        // addMarker() //add Marker in current position
        line = mMap.addPolyline(options) //add Polyline
    }

    fun getViewsAndInitListeners(){
        // Getting the View references
        accelerationXView = findViewById(R.id.accX_text_record)
        accelerationYView = findViewById(R.id.accY_text_record)
        accelerationZView = findViewById(R.id.accZ_text_record)

        // Getting ButtonView references
        startButton = findViewById(R.id.start_monitoring_button)
        stopButton = findViewById(R.id.stop_monitoring_button)

        // Setting ClickListeners on buttons
        startButton.setOnClickListener {
            measureDao?.deleteAllMeasures()

            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, arrayOf( android.Manifest.permission.ACCESS_FINE_LOCATION), GPS_REQUEST_CODE)
                return@setOnClickListener
            }
            // Starting Location updates
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

            // Starting acceleration updates
            this.accEventListener.register()

            startButton.isEnabled = false
            stopButton.isEnabled = true
        }
        stopButton.setOnClickListener {
            this.accEventListener.unregister()

            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, arrayOf( android.Manifest.permission.ACCESS_FINE_LOCATION), GPS_REQUEST_CODE)
                return@setOnClickListener
            }
            //Stop Location updates
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)

            // Showing the AlertDialog
            val saveDialog = YesNoDialog.newInstance("Title")
            saveDialog.show(supportFragmentManager,"save_lap" )

            startButton.isEnabled = true
            stopButton.isEnabled = false
        }
    }


}
