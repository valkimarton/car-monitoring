package hu.bme.aut.android.carmonitoringapp.database.type_converters

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hu.bme.aut.android.carmonitoringapp.model.Measure
import java.util.*

/* Defines how to convert complex type List<Measure> to a format (and back) which the DB can handle */
class MeasureListConverter {
    companion object {

        var gson = Gson()

        @TypeConverter
        @JvmStatic  // Java code sees method only with the help of this annotation !!!
        fun stringToMeasureList(data: String?): List<Measure> {
            if (data == null) {
                return Collections.emptyList()
            }

            val listType = object : TypeToken<List<Measure>>() {

            }.type

            return gson.fromJson(data, listType)
        }

        @TypeConverter
        @JvmStatic
        fun measureListToString(someObjects: List<Measure>): String {
            return gson.toJson(someObjects)
        }
    }
}