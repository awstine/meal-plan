package com.example.mealplanapp.ui.theme.data2

import com.example.mealplanapp.ui.theme.data2.DateTimeUtils.formatDateTime
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateTimeUtils {
    fun formatDateTime(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }
}


