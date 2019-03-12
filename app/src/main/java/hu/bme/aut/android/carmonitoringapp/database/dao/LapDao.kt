package hu.bme.aut.android.carmonitoringapp.database.dao

import android.arch.persistence.room.*
import hu.bme.aut.android.carmonitoringapp.model.Lap

@Dao
interface LapDao {

    /* Inserts one Lap, if the "name" is unique. Otherwise throws SQLiteConstraintException */
    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun insert(lap: Lap)

    /* Updates one lap. TODO: REPLACE does what update should generally do? What is a conflict when updating??? */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(lap: Lap)

    @Delete
    fun delete(lap: Lap)

    @Query("DELETE FROM Lap")
    fun deleteAllLaps()

    @Query("SELECT * FROM Lap WHERE name == :name")
    fun getLapByName(name: String) : Lap

    @Query("SELECT * FROM Lap ORDER BY date")
    fun getLaps(): List<Lap>
}