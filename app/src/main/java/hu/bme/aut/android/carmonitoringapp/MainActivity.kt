package hu.bme.aut.android.carmonitoringapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val monitoringButton: Button = findViewById(R.id.monitoring_button)
        monitoringButton.setOnClickListener {
            startActivity(Intent(this, RecordLapActivity::class.java))
        }

        val dashboardButton: Button = findViewById(R.id.dashboard_button)
        dashboardButton.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }

        val resultsButton: Button = findViewById(R.id.results_button)
        resultsButton.setOnClickListener { Toast.makeText(this, "Not implemented yet", Toast.LENGTH_SHORT).show() }

        println("PRINT\n\n PRINT\n" +
                "\nPRINT\n" +
                "\nPRINT\n" +
                "\nPRINT\n" +
                "\nPRINT\n" +
                "\n ")

    }
}
