package hu.bme.aut.android.carmonitoringapp.database

import android.database.Cursor
import android.os.AsyncTask
import android.util.Log
import hu.bme.aut.android.carmonitoringapp.RecordLapActivity

class LoadMeasuresTask(
    private val recordLapActivity: RecordLapActivity,
    private val dbLoader: MeasureDbLoader) : AsyncTask<Unit, Unit, Cursor>() {

    companion object {
        private const val TAG = "LoadMeasuresTask"
    }

    override fun doInBackground(vararg params: Unit): Cursor? {
        return try {

            println("LoadMeasureTask EXECUTING!!!")

            val result = dbLoader.fetchAll()

            if (!isCancelled) {
                result
            } else {
                Log.d(TAG, "Cancelled, closing cursor")
                result.close()
                null
            }
        } catch (e: Exception) {
            Log.d(TAG, "An error occurred while fetching Todos")
            null
        }
    }

    override fun onPostExecute(result: Cursor?) {
        Log.d(TAG, "Fetch completed, displaying cursor results")
        recordLapActivity.printMeasurePoints(result)
    }
}