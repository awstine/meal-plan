package com.example.mealplanapp.ui.theme.screen.health

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanapp.ui.theme.data.MealPlan
import com.example.mealplanapp.ui.theme.data2.AppDatabase
import com.example.mealplanapp.ui.theme.data2.MealPlanDao
import com.example.mealplanapp.ui.theme.data2.SavedMealPlan
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import javax.inject.Inject


@HiltViewModel
class HealthConditionViewModel @Inject constructor(
    application: Application,
    private val mealPlanDao: MealPlanDao // Inject the DAO
) : AndroidViewModel(application) {

    private val _mealPlan = mutableStateOf(
        MealPlan(
            breakfast = "Loading..." to "Loading...",
            lunch = "Loading..." to "Loading...",
            supper = "Loading..." to "Loading..."
        )
    )

    val mealPlan: State<MealPlan> = _mealPlan
    //private val database = AppDatabase.getDatabase(application)
    //private val mealPlanDao = database.mealPlanDao()
    private val appContext = application.applicationContext
    private val _toastMessage = mutableStateOf<String?>(null)
    val toastMessage: State<String?> = _toastMessage
    private val _customGoal = mutableStateOf("")
    val customGoal: State<String> = _customGoal

    fun setCustomGoal(goal: String) {
        _customGoal.value = goal
        generateMealPlan() // Auto-generate when goal is set
    }

    fun generateMealPlan() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val meals = loadMealsFromCSV()
                val goal = customGoal.value.lowercase()

                _mealPlan.value = when {
                    goal.contains("lose weight") || goal.contains("weight loss") ->
                        generateWeightLossMealPlan(meals)
                    goal.contains("gain weight") || goal.contains("weight gain") ->
                        generateWeightGainMealPlan(meals)
                    goal.contains("build muscle") || goal.contains("muscle gain") ->
                        generateMuscleGainMealPlan(meals)
                    else -> generateBalancedMealPlan(meals)
                }
            } catch (ioException: IOException) {
                // Handle CSV loading errors
                Log.e("MealPlanDebug", "Error loading CSV", ioException)
                _mealPlan.value = MealPlan(
                    breakfast = "Error loading meals" to "",
                    lunch = "Error loading meals" to "",
                    supper = "Error loading meals" to ""
                )
            } catch (e: Exception) {
                // Handle other unexpected errors
                Log.e("MealPlanDebug", "Unexpected error in generating meal plan", e)
                _mealPlan.value = MealPlan(
                    breakfast = "Error loading meals" to "",
                    lunch = "Error loading meals" to "",
                    supper = "Error loading meals" to ""
                )
            }
        }
    }


// Similarly update other generation functions

    init {
        loadInitialMealPlan()
    }

    init {
        generateMealPlan()
    }

    private fun loadInitialMealPlan() {
        viewModelScope.launch(Dispatchers.IO) {
            generateMealPlan()
        }
    }

    private fun selectRandomMeal(
        meals: Map<String, List<Pair<String, String>>>,
        categories: List<String>
    ): Pair<String, String> {
        return categories.flatMap { meals[it] ?: emptyList() }.randomOrNull() ?: ("No meal found" to "")
    }

    fun generateWeightGainMealPlan(meals: Map<String, List<Pair<String, String>>>): MealPlan {
        return MealPlan(
            breakfast = selectRandomMeal(meals, listOf("Bread", "Snack", "Beverage")),
            lunch = selectRandomMeal(meals, listOf("Main Dish", "Vegetarian")),
            supper = selectRandomMeal(meals, listOf("Main Dish", "Seafood", "Staple"))
        )
    }

    fun generateMuscleGainMealPlan(meals: Map<String, List<Pair<String, String>>>): MealPlan {
        return MealPlan(
            breakfast = selectRandomMeal(meals, listOf("Bread", "Snack", "Beverage")),
            lunch = selectRandomMeal(meals, listOf("Main Dish", "Vegetarian")),
            supper = selectRandomMeal(meals, listOf("Main Dish", "Seafood", "Staple"))
        )
    }

    fun generateBalancedMealPlan(meals: Map<String, List<Pair<String, String>>>): MealPlan {
        return MealPlan(
            breakfast = selectRandomMeal(meals, listOf("Bread", "Snack", "Beverage")),
            lunch = selectRandomMeal(meals, listOf("Main Dish", "Vegetarian", "Salad")),
            supper = selectRandomMeal(meals, listOf("Main Dish", "Seafood", "Staple"))
        )
    }
    fun generateWeightLossMealPlan(meals: Map<String, List<Pair<String, String>>>): MealPlan {
        return MealPlan(
            breakfast = selectRandomMeal(meals, listOf("Snack", "Beverage")),
            lunch = selectRandomMeal(meals, listOf("Vegetarian", "Salad")),
            supper = selectRandomMeal(meals, listOf("Main Dish", "Seafood"))
        )
    }




    private suspend fun loadMealsFromCSV(): Map<String, List<Pair<String, String>>> =
        withContext(Dispatchers.IO) {
            val mealCategories = mutableMapOf<String, MutableList<Pair<String, String>>>()

            try {
                appContext.assets.open("meal_plan.csv").use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        reader.readLine() // Skip header

                        reader.forEachLine { line ->
                            val parts = line.split(",").map { it.trim() }
                            if (parts.size >= 4) {
                                val category = parts[2]
                                val title = parts[1]
                                val ingredients = parts[3] // Get ingredients column
                                if (category.isNotEmpty() && title.isNotEmpty()) {
                                    mealCategories.getOrPut(category) { mutableListOf() }
                                        .add(title to ingredients)
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("MealPlanDebug", "Error loading CSV", e)
            }

            return@withContext mealCategories
        }


//    fun generateMealPlan() {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val meals = loadMealsFromCSV()
//
//                _mealPlan.value = MealPlan(
//                    breakfast = selectRandomMeals(meals, listOf("Bread", "Snack", "Beverage")),
//                    lunch = selectRandomMeals(meals, listOf("Main Dish", "Vegetarian", "Salad")),
//                    supper = selectRandomMeals(meals, listOf("Main Dish", "Seafood", "Staple"))
//                )
//            } catch (e: Exception) {
//                Log.e("MealPlanDebug", "Error generating meal plan", e)
//                _mealPlan.value = MealPlan(
//                    breakfast = "Error loading meals" to "",
//                    lunch = "Error loading meals" to "",
//                    supper = "Error loading meals" to ""
//                )
//            }
//        }
//    }

    fun saveCurrentMealPlan() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val current = mealPlan.value
                val savedMealPlan = SavedMealPlan(
                    breakfastName = current.breakfast.first,
                    breakfastIngredients = current.breakfast.second,
                    lunchName = current.lunch.first,
                    lunchIngredients = current.lunch.second,
                    supperName = current.supper.first,
                    supperIngredients = current.supper.second,
                    date = System.currentTimeMillis()
                )
                mealPlanDao.insert(savedMealPlan)
                Log.d("MealPlanDebug", "Saved meal plan: $savedMealPlan")
                showToast("Meal plan saved successfully!")
            } catch (e: Exception) {
                Log.e("MealPlanDebug", "Error saving meal plan", e)
                showToast("Error saving meal plan")
            }
        }
    }

    val allSavedMealPlans: Flow<List<SavedMealPlan>> = mealPlanDao.getAllSavedMealPlans()

    fun deleteMealPlan(mealPlan: SavedMealPlan) {
        viewModelScope.launch(Dispatchers.IO) {
            mealPlanDao.delete(mealPlan)
        }
    }

    private fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun clearToast() {
        _toastMessage.value = null
    }
}