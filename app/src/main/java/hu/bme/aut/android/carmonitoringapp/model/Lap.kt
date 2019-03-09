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
    @TypeConverters(MeasureListConverter::class)
    val measures: List<Measure>
    // val id: Long? = null     // Jo lesz a "name" kulcsnak, nem?
)
{

}