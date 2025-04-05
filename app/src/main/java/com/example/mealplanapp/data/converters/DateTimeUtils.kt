package com.example.mealplanapp.data.converters

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

object DateTimeUtils {
    // Define a formatter suitable for display
    @RequiresApi(Build.VERSION_CODES.O)
    private val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM) // e.g., Jan 5, 2024

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatLocalDate(date: LocalDate?): String {
        return date?.format(dateFormatter) ?: "Unknown Date"
    }
}


