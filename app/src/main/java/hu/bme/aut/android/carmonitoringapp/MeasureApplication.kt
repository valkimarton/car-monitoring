package hu.bme.aut.android.carmonitoringapp

import android.app.Application
import android.arch.persistence.room.Room
import hu.bme.aut.android.carmonitoringapp.database.MyDatabase

/*
* Az app indításakor példányosodik.
* Ez tartja fenn az adatbáziskapcsolatot reprezentáló MeasureDbLoader objektumot.
* Az app bezárásáig fut, akkor lezárja az adatbázis kapcsolatot.*/
class MeasureApplication : Application(){
    companion object {
        var db: MyDatabase? = null
    }

    override fun onCreate() {
        super.onCreate()

        db = MyDatabase.getMyDatabase(this)
    }

    override fun onTerminate() {
        super.onTerminate()

        db?.close() // TODO: kell? Vagy a Room magától zárja?
    }
}