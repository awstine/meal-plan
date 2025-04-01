package com.example.mealplanapp.ui.theme.data2

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader


class HealthConditionViewModel(application: Application) : AndroidViewModel(application) {
    private val _mealPlan = mutableStateOf(MealPlan(
        breakfast = "Loading...",
        lunch = "Loading...",
        supper = "Loading..."
    ))
    val mealPlan: State<MealPlan> = _mealPlan

    init {
        loadInitialMealPlan()
    }

    private fun loadInitialMealPlan() {
        viewModelScope.launch(Dispatchers.IO) {
            generateMealPlan(getApplication())
        }
    }

    fun generateMealPlan(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val meals = loadMealsFromCSV(context)
            _mealPlan.value = MealPlan(
                breakfast = meals["Bread"]?.randomOrNull() ?: "No Breakfast Available",
                lunch = meals["Main Dish"]?.randomOrNull() ?: "No Lunch Available",
                supper = meals["Meat"]?.randomOrNull() ?: meals["Seafood"]?.randomOrNull()
                ?: "No Supper Available"
            )
        }
    }

    private fun loadMealsFromCSV(context: Context): Map<String, List<String>> {
        val mealCategories = mutableMapOf<String, MutableList<String>>()

        try {
            context.assets.open("meal_plan.csv").use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readLine() // Skip header row

                    reader.forEachLine { line ->
                        val parts = line.split(",").map { it.trim() }
                        if (parts.size >= 3) { // Ensure we have at least ID, Title, Category
                            val category = parts[2] // Recipe Category is 3rd column
                            val title = parts[1] // Recipe Title is 2nd column

                            if (category.isNotEmpty() && title.isNotEmpty()) {
                                mealCategories.getOrPut(category) { mutableListOf() }.add(title)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MealPlanDebug", "Error loading CSV", e)
        }

        Log.d("MealPlanDebug", "Available categories: ${mealCategories.keys}")
        return mealCategories
    }
}