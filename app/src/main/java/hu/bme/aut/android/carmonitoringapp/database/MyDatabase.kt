package hu.bme.aut.android.carmonitoringapp.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import hu.bme.aut.android.carmonitoringapp.database.dao.MeasureDao
import hu.bme.aut.android.carmonitoringapp.model.Measure

@Database(entities = [Measure::class], version = DbConstants.DATABASE_VERSION)
abstract class MyDatabase: RoomDatabase() {

    abstract fun measureDao(): MeasureDao

    companion object {
        private var INSTANCE: MyDatabase? = null

        fun getMyDatabase(context: Context): MyDatabase? {
            if (INSTANCE == null){
                synchronized(MyDatabase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext, MyDatabase::class.java, "myDB").allowMainThreadQueries().build() //TODO: !!! FIX THIS "allowmain..."
                }
            }
            return INSTANCE
        }

        fun destroyDataBase(){
            INSTANCE = null
        }
    }
}