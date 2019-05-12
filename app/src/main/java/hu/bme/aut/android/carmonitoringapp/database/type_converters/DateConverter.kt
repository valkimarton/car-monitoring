package hu.bme.aut.android.carmonitoringapp.database.type_converters

import android.arch.persistence.room.TypeConverter
import java.util.*

class DateConverter {
    @TypeConverter
    fun timeStampToDate(value: Long?) : Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimeStamp(date: Date?) : Long? {
        return date?.time
    }
}