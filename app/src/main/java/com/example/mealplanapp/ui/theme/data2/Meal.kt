package com.example.mealplanapp.ui.theme.data2

// model/Meal.kt
data class Meal(
    val id: Int,
    val name: String,
    val imageResId: Int, // Reference to drawable resource
    val description: String,
    val category: String, // Breakfast, Lunch, Dinner
    val calories: Int,
    val ingredients: List<String>
)