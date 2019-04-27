package hu.bme.aut.android.carmonitoringapp

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import hu.bme.aut.android.carmonitoringapp.adapter.LapAdapter
import hu.bme.aut.android.carmonitoringapp.database.MyDatabase
import hu.bme.aut.android.carmonitoringapp.database.dao.LapDao
import hu.bme.aut.android.carmonitoringapp.model.Lap

class ResultsActivity : AppCompatActivity() {

    private var db: MyDatabase? = null
    private var lapDao: LapDao? = null

    private lateinit var lapList: List<Lap>

    private val EXTERNAL_STORAGE_PERMISSION: Int = 1001

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

        // Getting storage perission
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.v("permission","Permission needed");
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), EXTERNAL_STORAGE_PERMISSION);
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            EXTERNAL_STORAGE_PERMISSION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d("permission", "permission granted")
                    // DO stuff
                } else {
                    Toast.makeText(this, "Exporting results disallowed", Toast.LENGTH_LONG)
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }
}
