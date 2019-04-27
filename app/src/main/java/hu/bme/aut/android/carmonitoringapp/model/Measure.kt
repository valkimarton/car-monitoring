package hu.bme.aut.android.carmonitoringapp.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Measure(
    var latitude: Double,
    var longitude: Double,
    var accX: Double,
    var accY: Double,
    var accZ: Double,
    var time: Double,
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null
)
{
    override fun toString(): String {
        return "Time: ${time} sec," +
                "Lat: ${latitude}, " +
                "Long: ${longitude}, " +
                "accX: ${accX}, " +
                "accY: ${accY}, " +
                "accZ: ${accZ}, " +
                "id: ${id}"
    }
}