package hu.bme.aut.android.carmonitoringapp

import android.database.Cursor
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import hu.bme.aut.android.carmonitoringapp.database.DbConstants
import hu.bme.aut.android.carmonitoringapp.database.LoadMeasuresTask
import hu.bme.aut.android.carmonitoringapp.database.MeasureDbLoader
import hu.bme.aut.android.carmonitoringapp.model.Measure

import kotlinx.android.synthetic.main.activity_record_lap.*

class RecordLapActivity : AppCompatActivity() {

    private var measurementsCursor: Cursor? = null //TODO: Do we need this?

    private lateinit var dbLoader: MeasureDbLoader

    private var loadMeasuresTask: LoadMeasuresTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_lap)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dbLoader = MeasureApplication.measureDbLoader

        createSampleMeasures()

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
                println(getMeasureByCursor(measurements).toString())
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

//TODO: This is a copy of MeasureDbLoader.getMeasureByCursor (in companion object). Should call that one.
    fun getMeasureByCursor(c: Cursor): Measure {
        return Measure(
            id = c.getLong(c.getColumnIndex(DbConstants.Measure.KEY_ROWID)),
            latitude = c.getDouble(c.getColumnIndex(DbConstants.Measure.KEY_LATITUDE)),
            longitude = c.getDouble(c.getColumnIndex(DbConstants.Measure.KEY_LATITUDE)),
            accX = c.getDouble(c.getColumnIndex(DbConstants.Measure.KEY_ACCX)),
            accY = c.getDouble(c.getColumnIndex(DbConstants.Measure.KEY_ACCY)),
            accZ = c.getDouble(c.getColumnIndex(DbConstants.Measure.KEY_ACCZ)),
            time = c.getDouble(c.getColumnIndex(DbConstants.Measure.KEY_TIME))
        )
    }

}
