package hu.bme.aut.android.carmonitoringapp

import android.app.Application
import hu.bme.aut.android.carmonitoringapp.database.MeasureDbLoader

class MeasureApplication : Application(){
    companion object {
        lateinit var measureDbLoader: MeasureDbLoader
            private set
    }

    override fun onCreate() {
        super.onCreate()

        measureDbLoader = MeasureDbLoader(this)
        measureDbLoader.open()
    }

    override fun onTerminate() {
        measureDbLoader.close()
        super.onTerminate()
    }
}