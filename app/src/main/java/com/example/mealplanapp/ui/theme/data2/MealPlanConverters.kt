package com.example.mealplanapp.ui.theme.data

import androidx.room.TypeConverter

class MealPlanConverters {
    @TypeConverter
    fun fromPair(pair: Pair<String, String>): String {
        return "${pair.first},${pair.second}"
    }

    @TypeConverter
    fun toPair(value: String): Pair<String, String> {
        val parts = value.split(",")
        return Pair(parts[0], parts[1])
    }
}
