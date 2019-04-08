package hu.bme.aut.android.carmonitoringapp.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import hu.bme.aut.android.carmonitoringapp.model.Lap
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import com.opencsv.CSVWriter
import com.opencsv.bean.ColumnPositionMappingStrategy
import com.opencsv.bean.StatefulBeanToCsv
import com.opencsv.bean.StatefulBeanToCsvBuilder
import hu.bme.aut.android.carmonitoringapp.R
import hu.bme.aut.android.carmonitoringapp.model.Measure
import java.io.File
import java.io.FileWriter
import java.io.IOException


public class LapAdapter(context: Context, resource: Int, laps: ArrayList<Lap>): ArrayAdapter<Lap>(context, resource, laps) {

    var resource: Int
    var laps: ArrayList<Lap>
    var layoutInflater: LayoutInflater

    init {
        this.resource = resource
        this.laps = laps
        this.layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    }
    // Returns the actual View to use in the ListView at a certain row position
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // getting the matching 'Lap' data object from the list
        val lap: Lap = getItem(position)

        // View to be returned. (Needed becouse parameter (convertView) cant be reassigned)
        var returnedView: View

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            returnedView = LayoutInflater.from(getContext()).inflate(R.layout.result_lap, parent, false)
        } else {
            returnedView = convertView
        }

        // getting the subviews for data population (a view-ok föltöltése a Lap object adataival)
        val tvName: TextView = returnedView.findViewById(R.id.result_lap_name)
        val tvDate: TextView = returnedView.findViewById(R.id.result_lap_date)
        val tvCount: TextView = returnedView.findViewById(R.id.result_lap_measurement_count)

        // Populate the data into the template view using the data object
        tvName.setText(lap.name)
        tvDate.setText(lap.date.toString())
        tvCount.setText(lap.measures.size.toString())


        // setting clickListener on export button
        val bExportButton: Button = returnedView.findViewById(R.id.result_lap_export_button)
        bExportButton.setOnClickListener {

            val file = File(context.filesDir, "measures.csv")

            var fileWriter: FileWriter? = null
            var csvWriter: CSVWriter? = null

            try {
                fileWriter = FileWriter(file)

                // write String Array
                csvWriter = CSVWriter(fileWriter,
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END)

                val CSV_HEADER = arrayOf<String>("id", "latitude", "longitude", "acc-x", "acc-y", "acc-z", "time")
                csvWriter.writeNext(CSV_HEADER)

                for (measure in lap.measures) {
                    val data = arrayOf<String>(
                        measure.id!!.toString(),
                        measure.latitude.toString(),
                        measure.longitude.toString(),
                        measure.accX.toString(),
                        measure.accY.toString(),
                        measure.accZ.toString(),
                        measure.time.toString()
                    )

                    csvWriter.writeNext(data)
                }

                println(file.absolutePath)
                println("Write CSV using CSVWriter successfully!")
            } catch (e: Exception) {
                println("Writing CSV error!")
                e.printStackTrace()
            } finally {
                try {
                    fileWriter!!.flush()
                    csvWriter!!.close()
                    fileWriter.close()
                } catch (e: IOException) {
                    println("Flushing/closing error!")
                    e.printStackTrace()
                }
            }
        }

        return returnedView
    }
}