package hu.bme.aut.android.carmonitoringapp.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import hu.bme.aut.android.carmonitoringapp.database.type_converters.MeasureListConverter
import java.util.*

@Entity(indices = arrayOf(Index(value = ["name"], unique = true))) // Index for "name", plus making it unique
data class Lap(
    @PrimaryKey
    val name: String,
    val date: Date,
    val measures: List<Measure>
    // val id: Long? = null     // Jo lesz a "name" kulcsnak, nem?
)
{
    override fun toString(): String {
        var result: String =
            "LAP:\n" +
            "NAME: " + this.name + "\n" +
            "DATE: " + this.date.toString() + "\n" +
            "MEASURE POINTS: " + this.measures.size
        return result;
    }
}