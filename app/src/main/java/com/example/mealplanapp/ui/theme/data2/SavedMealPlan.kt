package com.example.mealplanapp.ui.theme.data2

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_meal_plans")
data class SavedMealPlan(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: Long = System.currentTimeMillis(),  // Renamed from 'date' to 'timestamp'
    val breakfastName: String,
    val breakfastIngredients: String,
    val lunchName: String,
    val lunchIngredients: String,
    val supperName: String,
    val supperIngredients: String
)