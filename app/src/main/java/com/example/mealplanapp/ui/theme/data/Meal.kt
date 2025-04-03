package com.example.mealplanapp.ui.theme.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

// model/Meal.kt
@Entity(tableName = "meals")
data class Meal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val imageResId: Int, // Reference to drawable resource
    val description: String,
    val category: String, // Breakfast, Lunch, Dinner
    val calories: Int,
    val ingredients: List<String>,
    val instructions: String = ""
)



@Entity
@TypeConverters(MealPlanConverters::class)
data class MealPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Primary Key
    val breakfast: Pair<String, String>,
    val lunch: Pair<String, String>,
    val supper: Pair<String, String>
)


data class SavedMeal(
    val time: String,
    val name: String,
    val ingredients: String
)

// For CSV parsing
data class CsvRecipe(
    val id: Int,
    val title: String,
    val category: String,
    val ingredients: String,
    val instructions: String
)