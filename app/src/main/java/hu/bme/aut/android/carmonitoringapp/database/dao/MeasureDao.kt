package hu.bme.aut.android.carmonitoringapp.database.dao

import android.arch.persistence.room.*
import hu.bme.aut.android.carmonitoringapp.model.Measure

@Dao
interface MeasureDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) // TODO: Ez lehet más stratégia is, ha az kell
    fun insertMeasure(measure: Measure)

    @Update
    fun updateMeasure(measure: Measure)

    @Delete
    fun deleteMeasure(measure: Measure)

    @Query("DELETE FROM Measure")
    fun deleteAllMeasures()

    @Query("SELECT * FROM Measure WHERE id == :id")
    fun getMeasureById(id: Int): Measure

    @Query("SELECT * FROM Measure ORDER BY time")
    fun getMeasures(): List<Measure>
}