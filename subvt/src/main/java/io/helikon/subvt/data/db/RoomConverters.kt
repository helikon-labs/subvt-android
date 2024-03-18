package io.helikon.subvt.data.db

import androidx.room.TypeConverter
import java.util.Date

class RoomConverters {
    @TypeConverter
    fun dateFromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}
