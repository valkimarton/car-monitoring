package hu.bme.aut.android.carmonitoringapp

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import hu.bme.aut.android.carmonitoringapp.database.DbConstants
import hu.bme.aut.android.carmonitoringapp.database.MyDatabase
import hu.bme.aut.android.carmonitoringapp.database.dao.MeasureDao
import hu.bme.aut.android.carmonitoringapp.fragments.YesNoDialog
import hu.bme.aut.android.carmonitoringapp.model.Measure
import hu.bme.aut.android.carmonitoringapp.sensor.AccEventListener

import kotlinx.android.synthetic.main.activity_record_lap.*

class RecordLapActivity : AppCompatActivity() {

    // Database stuffs
    private var db: MyDatabase? = null
    private var measureDao: MeasureDao? = null


    // Sensor stuffs
    private lateinit var accEventListener: AccEventListener

    // Views
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
        db = MeasureApplication.db
        measureDao = db?.measureDao()

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
        stopButton.setOnClickListener {
            this.accEventListener.unregister()

            // Showing the AlertDialog
            val saveDialog = YesNoDialog.newInstance("Title")
            saveDialog.show(supportFragmentManager,"save_lap" )
        }
        printResultButton.setOnClickListener {
            val measures: List<Measure>? = measureDao?.getMeasures()
            this.printMeasurePoints(measures)

        }
        clearDbButton.setOnClickListener { measureDao?.deleteAllMeasures() }

        // Initializing the AccEventListener. "this" context needed for getSystemSercive -> sensorManager
        accEventListener = AccEventListener(this, accelerationXView, accelerationYView, accelerationZView)

        //createSampleMeasures()

    }


    fun printMeasurePoints(measurements: List<Measure>?) {
        measurements?.forEach { println(it) }
    }
}
