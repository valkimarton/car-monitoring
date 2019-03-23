package hu.bme.aut.android.carmonitoringapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import hu.bme.aut.android.carmonitoringapp.adapter.LapAdapter
import hu.bme.aut.android.carmonitoringapp.database.MyDatabase
import hu.bme.aut.android.carmonitoringapp.database.dao.LapDao
import hu.bme.aut.android.carmonitoringapp.model.Lap

class ResultsActivity : AppCompatActivity() {

    private var db: MyDatabase? = null
    private var lapDao: LapDao? = null

    private lateinit var lapList: List<Lap>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        // Database connection handler
        db = MeasureApplication.db
        lapDao = db?.lapDao()

        // Reading Laps from database
        lapList = lapDao?.getLaps() ?: ArrayList<Lap>()

        // Creating adapter
        val lapAdapter: LapAdapter = LapAdapter(this, 0, ArrayList(lapList))

        // Attaching the adapter to the listView
        val listView: ListView = findViewById(R.id.result_listview)
        listView.adapter = lapAdapter
    }
}
