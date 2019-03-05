package hu.bme.aut.android.carmonitoringapp.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import hu.bme.aut.android.carmonitoringapp.model.Measure

class MeasureDbLoader(private val context: Context) {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var db: SQLiteDatabase

    @Throws(SQLException::class)
    fun open() {
        dbHelper = DatabaseHelper(context, DbConstants.DATABASE_NAME)
        db = dbHelper.writableDatabase

        dbHelper.onCreate(db)
    }

    fun close() {
        dbHelper.close()
    }

    // CRUD és egyéb metódusok

    // INSERT
    fun createMeasure(measure: Measure): Long {
        val values = ContentValues()
        values.put(DbConstants.Measure.KEY_LATITUDE, measure.latitude)
        values.put(DbConstants.Measure.KEY_LONGITUDE, measure.longitude)
        values.put(DbConstants.Measure.KEY_ACCX, measure.accX)
        values.put(DbConstants.Measure.KEY_ACCY, measure.accY)
        values.put(DbConstants.Measure.KEY_ACCZ, measure.accZ)
        values.put(DbConstants.Measure.KEY_TIME, measure.time)

        return db.insert(DbConstants.Measure.DATABASE_TABLE, null, values)
    }

    // DELETE
    fun deleteMeasure(rowId: Long): Boolean {
        val deletedMeasures = db.delete(
            DbConstants.Measure.DATABASE_TABLE,
            "${DbConstants.Measure.KEY_ROWID} = $rowId",
            null
        )
        return deletedMeasures > 0
    }

    // UPDATE
    fun updateMeasure(newMeasure: Measure): Boolean {
        val values = ContentValues()
        values.put(DbConstants.Measure.KEY_LATITUDE, newMeasure.latitude)
        values.put(DbConstants.Measure.KEY_LONGITUDE, newMeasure.longitude)
        values.put(DbConstants.Measure.KEY_ACCX, newMeasure.accX)
        values.put(DbConstants.Measure.KEY_ACCY, newMeasure.accY)
        values.put(DbConstants.Measure.KEY_ACCZ, newMeasure.accZ)
        values.put(DbConstants.Measure.KEY_TIME, newMeasure.time)

        val measuresUpdated = db.update(
            DbConstants.Measure.DATABASE_TABLE,
            values,
            "${DbConstants.Measure.KEY_ROWID} = ${newMeasure.id}",
            null
        )

        return measuresUpdated > 0
    }

    // Clear all measurements
    fun clearAll() {
        db.execSQL("delete from "+ DbConstants.Measure.DATABASE_TABLE)
    }

    // Get all Measurements
    fun fetchAll(): Cursor {
        // Cursor pointing to the result set of all Measurements with all fields (where = null)
        return db.query(
            DbConstants.Measure.DATABASE_TABLE,
            arrayOf(
                DbConstants.Measure.KEY_ROWID,
                DbConstants.Measure.KEY_LATITUDE,
                DbConstants.Measure.KEY_LONGITUDE,
                DbConstants.Measure.KEY_ACCX,
                DbConstants.Measure.KEY_ACCY,
                DbConstants.Measure.KEY_ACCZ,
                DbConstants.Measure.KEY_TIME
            ),
            null,
            null,
            null,
            null,
            DbConstants.Measure.KEY_TIME
        )
    }

    // Querying for one of the Measurements with the given id
    fun fetchMeasure(id: Long): Measure? {
        // Cursor pointing to a result set with 0 or 1 Measurement
        val cursor = db.query(
            DbConstants.Measure.DATABASE_TABLE,
            arrayOf(
                DbConstants.Measure.KEY_ROWID,
                DbConstants.Measure.KEY_LATITUDE,
                DbConstants.Measure.KEY_LONGITUDE,
                DbConstants.Measure.KEY_ACCX,
                DbConstants.Measure.KEY_ACCY,
                DbConstants.Measure.KEY_ACCZ,
                DbConstants.Measure.KEY_TIME
            ),
            "${DbConstants.Measure.KEY_ROWID} = $id",
            null,
            null,
            null,
            DbConstants.Measure.KEY_TIME
        )

        // Return with the found entry or null if there wasn't any with the given id
        return if (cursor.moveToFirst()) {
            getMeasureByCursor(cursor)
        } else {
            null
        }
    }

    companion object {
        fun getMeasureByCursor(c: Cursor): Measure {

            return Measure(
                id = c.getLong(c.getColumnIndex(DbConstants.Measure.KEY_ROWID)),
                latitude = c.getDouble(c.getColumnIndex(DbConstants.Measure.KEY_LATITUDE)),
                longitude = c.getDouble(c.getColumnIndex(DbConstants.Measure.KEY_LATITUDE)),
                accX = c.getDouble(c.getColumnIndex(DbConstants.Measure.KEY_ACCX)),
                accY = c.getDouble(c.getColumnIndex(DbConstants.Measure.KEY_ACCY)),
                accZ = c.getDouble(c.getColumnIndex(DbConstants.Measure.KEY_ACCZ)),
                time = c.getDouble(c.getColumnIndex(DbConstants.Measure.KEY_TIME))
            )
        }
    }
}