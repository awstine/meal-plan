package com.example.mealplanapp.ui.theme.data2

// Replace with your actual package name
import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.util.Date

//class Converters {
//    @TypeConverter
//    fun fromTimestamp(value: Long?): Date? {
//        return value?.let { Date(it) }
//    }
//
//    @TypeConverter
//    fun dateToTimestamp(date: Date?): Long? {
//        return date?.time
//    }
//}


@ProvidedTypeConverter
class Converters {
    @TypeConverter
    fun fromList(value: List<String>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toList(value: String): List<String> {
        return value.split(",")
    }
}
