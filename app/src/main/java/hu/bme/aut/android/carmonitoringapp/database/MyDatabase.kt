package hu.bme.aut.android.carmonitoringapp.database

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.arch.persistence.room.migration.Migration
import android.content.Context
import hu.bme.aut.android.carmonitoringapp.database.dao.LapDao
import hu.bme.aut.android.carmonitoringapp.database.dao.MeasureDao
import hu.bme.aut.android.carmonitoringapp.database.type_converters.DateConverter
import hu.bme.aut.android.carmonitoringapp.database.type_converters.MeasureListConverter
import hu.bme.aut.android.carmonitoringapp.model.Lap
import hu.bme.aut.android.carmonitoringapp.model.Measure

@Database(entities = [Measure::class, Lap::class], version = DbConstants.DATABASE_VERSION)
@TypeConverters(DateConverter::class, MeasureListConverter::class)
abstract class MyDatabase: RoomDatabase() {

    abstract fun measureDao(): MeasureDao
    abstract fun lapDao(): LapDao

    companion object {
        private var INSTANCE: MyDatabase? = null

        val MIGRATION_1_2: Migration = object : Migration(1,2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `Lap` (`name` TEXT NOT NULL, `date` INTEGER NOT NULL, `measures` TEXT NOT NULL, PRIMARY KEY(`name`))")
                database.execSQL("CREATE UNIQUE INDEX index_Lap_name ON Lap (name)");
            }
        }

        fun getMyDatabase(context: Context): MyDatabase? {
            if (INSTANCE == null){
                synchronized(MyDatabase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext, MyDatabase::class.java, "myDB").addMigrations(
                        MIGRATION_1_2).allowMainThreadQueries().build() //TODO: !!! FIX THIS "allowmain..."
                }
            }
            return INSTANCE
        }

        fun destroyDataBase(){
            INSTANCE = null
        }
    }
}