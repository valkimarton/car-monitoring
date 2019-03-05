package hu.bme.aut.android.carmonitoringapp

import android.content.Context
import android.database.Cursor
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import hu.bme.aut.android.carmonitoringapp.database.DbConstants
import hu.bme.aut.android.carmonitoringapp.database.LoadMeasuresTask
import hu.bme.aut.android.carmonitoringapp.database.MeasureDbLoader
import hu.bme.aut.android.carmonitoringapp.model.Measure
import hu.bme.aut.android.carmonitoringapp.sensor.AccEventListener

import kotlinx.android.synthetic.main.activity_record_lap.*

class RecordLapActivity : AppCompatActivity() {

    // Database stuffs

    private var measurementsCursor: Cursor? = null //TODO: Do we need this?
    private lateinit var dbLoader: MeasureDbLoader
    private var loadMeasuresTask: LoadMeasuresTask? = null


    //Sensor stuffs

    private lateinit var sensorManager: SensorManager
    private lateinit var accEventListener: AccEventListener

    private lateinit var accelerationXView: TextView
    private lateinit var accelerationYView: TextView
    private lateinit var accelerationZView: TextView

    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var printResultButton: Button
    private lateinit var clearDbButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_lap)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Database connection handler
        dbLoader = MeasureApplication.measureDbLoader

        // Getting the View references
        accelerationXView = findViewById(R.id.accX_text_record)
        accelerationYView = findViewById(R.id.accY_text_record)
        accelerationZView = findViewById(R.id.accZ_text_record)

        // Getting ButtonView references
        startButton = findViewById(R.id.start_monitoring_button)
        stopButton = findViewById(R.id.stop_monitoring_button)
        printResultButton = findViewById(R.id.print_db_content_button)
        clearDbButton = findViewById(R.id.clear_db_button)

        // Setting ClickListeners on buttons
        startButton.setOnClickListener { this.accEventListener.register() }
        stopButton.setOnClickListener { this.accEventListener.unregister() }
        printResultButton.setOnClickListener {
            val measures: Cursor = this.dbLoader.fetchAll()
            this.printMeasurePoints(measures)
        }
        clearDbButton.setOnClickListener { this.dbLoader.clearAll() }

        // Initializing the AccEventListener. "this" context needed for getSystemSercive -> sensorManager
        accEventListener = AccEventListener(this, accelerationXView, accelerationYView, accelerationZView)

        //createSampleMeasures()

    }

    override fun onResume() {
        super.onResume()

        refreshList()
    }

    override fun onPause() {
        super.onPause()

        loadMeasuresTask?.cancel(false)
    }

    override fun onDestroy() {
        super.onDestroy()

        measurementsCursor?.close() //TODO: close cursor !!!
    }

    private fun refreshList() { //TODO: megérteni
        loadMeasuresTask?.cancel(false)

        val loadMeasuresTaskNew = LoadMeasuresTask(this, dbLoader)
        loadMeasuresTaskNew.execute()

        this.loadMeasuresTask = loadMeasuresTaskNew //TODO: megérteni
    }

    fun printMeasurePoints(measurements: Cursor?) {
        if (measurements != null) {
            //adapter = TodoAdapter(applicationContext, todos)
            //setupRecyclerView()

            println("No. of measurements: ${measurements.count} !!!")

            measurementsCursor = measurements // TODO: fölösleges
            measurements.moveToFirst()
            while (!measurements.isAfterLast()) {
                println(MeasureDbLoader.getMeasureByCursor(measurements).toString())
                measurements.moveToNext()
            }
        } else {
            println("There are no measurements to print!!!")
        }
        loadMeasuresTask = null
    }

    private fun createSampleMeasures() {
        this.dbLoader.createMeasure( measure = Measure(123.123, 11.11, 5.0, 5.0, 5.0, 30.0) )
        this.dbLoader.createMeasure( measure = Measure(123.123, 11.11, 5.0, 5.0, 5.0, 31.0) )
        this.dbLoader.createMeasure( measure = Measure(123.123, 11.11, 5.0, 5.0, 5.0, 32.0) )
        this.dbLoader.createMeasure( measure = Measure(123.123, 11.11, 5.0, 5.0, 5.0, 29.0) )

    }

}
