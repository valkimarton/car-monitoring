package hu.bme.aut.android.carmonitoringapp.database

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration

class Migrations {
    val MIGRATION_1_2: Migration = object : Migration(1,2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `Lap` (`name` TEXT NOT NULL, `date` INTEGER NOT NULL, `measures` TEXT NOT NULL, PRIMARY KEY(`name`))")
        }
    }
}