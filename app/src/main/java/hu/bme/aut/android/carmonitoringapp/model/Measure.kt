package hu.bme.aut.android.carmonitoringapp.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Measure(
    val latitude: Double,
    val longitude: Double,
    val accX: Double,
    val accY: Double,
    val accZ: Double,
    val time: Double,
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null
)
{
    override fun toString(): String {
        return "Time: ${time} sec," +
                "Lat: ${latitude}, " +
                "Alt: ${longitude}, " +
                "accX: ${accX}, " +
                "accY: ${accY}, " +
                "accZ: ${accZ}, " +
                "id: ${id}"
    }
}