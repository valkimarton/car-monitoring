package hu.bme.aut.android.carmonitoringapp.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(
    context: Context,
    name: String
) : SQLiteOpenHelper(context, name, null, DbConstants.DATABASE_VERSION){

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(DbConstants.DATABASE_CREATE_ALL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(DbConstants.DATABASE_DROP_ALL)
        db.execSQL(DbConstants.DATABASE_CREATE_ALL)
    }


}