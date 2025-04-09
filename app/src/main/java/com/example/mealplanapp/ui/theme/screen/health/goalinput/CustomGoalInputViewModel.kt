package com.example.mealplanapp.ui.theme.screen.health.goalinput

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanapp.data.entity.Meal // Import Meal instead of MealEntity
import com.example.mealplanapp.data.entity.SavedMealPlan
import com.example.mealplanapp.data.model.MealPlan // Assuming this is defined correctly elsewhere
import com.example.mealplanapp.data.repository.MealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject


// --- NEW: Data class to hold the generated meal details ---
data class GeneratedPlanDetails(
    val breakfast: List<Meal> = emptyList(),
    val lunch: List<Meal> = emptyList(),
    val supper: List<Meal> = emptyList()
)

// --- MODIFIED: UI State ---
data class CustomInputUiState(
    val goal: String = "",
    // val mealPlan: MealPlan? = null, // OLD: Remove MealPlan (String pairs)
    val generatedPlan: GeneratedPlanDetails? = null, // NEW: Holds actual Meal objects
    val isLoading: Boolean = false,
    val error: CustomInputError? = null,
    val saveSuccess: Boolean = false // Optional: For showing save confirmation
)

sealed class CustomInputError {
    data class MealPlanGenerationFailed(val message: String) : CustomInputError()
    // Add other specific errors if needed
}

sealed class CustomInputEvent {
    data class OnGoalChanged(val goal: String) : CustomInputEvent()
    data object Submit : CustomInputEvent()
    data object ResetSaveStatus : CustomInputEvent() // To clear success message
}



class CustomGoalInputViewModel ( // Use constructor injection with Hilt
    private val mealRepository: MealRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CustomInputUiState())
    val state: StateFlow<CustomInputUiState> = _state.asStateFlow()

    // --- Keep track of all meals loaded ---
    // You might already have this in HealthConditionViewModel, consider sharing via repository or a shared ViewModel if needed
    // For simplicity here, let's assume we load them when needed for generation
    // private val _allMeals = MutableStateFlow<List<Meal>>(emptyList()) // Alternative approach

    init {
        // Optional: Preload meals if needed, though generateMealPlan also loads them
        // preloadAllMeals()
    }

    // private fun preloadAllMeals() {
    //     viewModelScope.launch {
    //         // Example: Fetch only once if needed
    //         if (_allMeals.value.isEmpty()) {
    //            _allMeals.value = mealRepository.loadMealsFromCSV() // Or from DB
    //         }
    //     }
    // }


    fun onEvent(event: CustomInputEvent) {
        when (event) {
            is CustomInputEvent.OnGoalChanged -> {
                // Reset generated plan and error when goal changes
                _state.value = state.value.copy(
                    goal = event.goal,
                    generatedPlan = null,
                    error = null,
                    saveSuccess = false
                )
            }
            is CustomInputEvent.Submit -> {
                generateMealPlan()
            }
            is CustomInputEvent.ResetSaveStatus -> {
                _state.value = state.value.copy(saveSuccess = false)
            }
        }
    }

    private fun generateMealPlan() {
        _state.value = state.value.copy(isLoading = true, generatedPlan = null, error = null)

        viewModelScope.launch {
            try {
                val meals = mealRepository.getAllMeals().first() // Get from DB instead of CSV
                Log.d("MealGeneration", "Total meals loaded: ${meals.size}")

                if (meals.isEmpty()) {
                    _state.value = state.value.copy(
                        error = CustomInputError.MealPlanGenerationFailed("No meals available"),
                        isLoading = false
                    )
                    return@launch
                }

                // Debug: Log all available categories
                val allCategories = meals.map { it.category }.distinct()
                Log.d("MealGeneration", "Available categories: $allCategories")

                val goal = _state.value.goal.lowercase().trim()
                val categories = when {
                    goal.contains("lose weight") -> listOf("Breakfast", "Lunch", "Dinner", "Salad", "Vegetarian")
                    goal.contains("gain muscle") -> listOf("Breakfast", "Lunch", "Dinner", "Main Dish", "Protein")
                    else -> listOf("Breakfast", "Lunch", "Dinner", "Main Dish")
                }

                // Get meals for each type with fallback logic
                val breakfast = findMealByType(meals, "Breakfast", categories)
                val lunch = findMealByType(meals, "Lunch", categories)
                    ?: findMealByType(meals, "Main Dish", categories) // Fallback
                val supper = findMealByType(meals, "Dinner", categories)
                    ?: findMealByType(meals, "Supper", categories)
                    ?: findMealByType(meals, "Main Dish", categories) // Fallback

                if (breakfast == null || lunch == null || supper == null) {
                    val missing = listOfNotNull(
                        if (breakfast == null) "Breakfast" else null,
                        if (lunch == null) "Lunch" else null,
                        if (supper == null) "Dinner/Supper" else null
                    ).joinToString()

                    Log.w("MealGeneration", "Missing meals for: $missing")
                    _state.value = state.value.copy(
                        error = CustomInputError.MealPlanGenerationFailed("Couldn't find meals for: $missing"),
                        isLoading = false
                    )
                    return@launch
                }

                _state.value = state.value.copy(
                    generatedPlan = GeneratedPlanDetails(
                        breakfast = listOf(breakfast),
                        lunch = listOf(lunch),
                        supper = listOf(supper)
                    ),
                    isLoading = false
                )

            } catch (e: Exception) {
                Log.e("MealGeneration", "Error", e)
                _state.value = state.value.copy(
                    error = CustomInputError.MealPlanGenerationFailed(e.message ?: "Error"),
                    isLoading = false
                )
            }
        }
    }




    // --- MODIFIED: Select random meals, optionally filtering by a specific meal type ---
    private fun selectRandomMeals(
        allMeals: List<Meal>,
        allowedCategories: List<String>,
        mealType: String? = null // Optional: Filter by "Breakfast", "Lunch", "Supper", etc.
    ): List<Meal> {
        val allowedCategoriesLower = allowedCategories.map { it.lowercase() }.toSet()

        val potentialMeals = allMeals.filter { meal ->
            // Check if category is allowed
            val categoryMatch = meal.category.lowercase() in allowedCategoriesLower
            // If mealType is specified, check if it matches (case-insensitive)
            val mealTypeMatch = mealType == null || meal.category.equals(mealType, ignoreCase = true)
            categoryMatch && mealTypeMatch
        }

        Log.d("SelectRandomMeals", "Found ${potentialMeals.size} potential meals for MealType: $mealType with Categories: $allowedCategories")


        // Return shuffled list (caller can decide how many to take)
        return potentialMeals.shuffled()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun saveCurrentMealPlan() {
        viewModelScope.launch {
            val planDetails = state.value.generatedPlan ?: run {
                _state.value = state.value.copy(
                    error = CustomInputError.MealPlanGenerationFailed("No plan to save"),
                    saveSuccess = false
                )
                return@launch
            }

            val breakfast = planDetails.breakfast.firstOrNull()
            val lunch = planDetails.lunch.firstOrNull()
            val supper = planDetails.supper.firstOrNull()

            if (breakfast == null || lunch == null || supper == null) {
                Log.w("MealSaving", "Null meals - B:${breakfast?.id}, L:${lunch?.id}, S:${supper?.id}")
                _state.value = state.value.copy(
                    error = CustomInputError.MealPlanGenerationFailed("Complete plan required"),
                    saveSuccess = false
                )
                return@launch
            }

            try {
                // Ensure meals exist in DB first
                mealRepository.insertMeal(breakfast)
                mealRepository.insertMeal(lunch)
                mealRepository.insertMeal(supper)

                val savedPlan = SavedMealPlan(
                    date = LocalDate.now(),
                    breakfastId = breakfast.id,
                    lunchId = lunch.id,
                    supperId = supper.id
                )

                mealRepository.insertSavedMealPlan(savedPlan)
                Log.i("MealSaving", "Saved plan: $savedPlan")

                _state.value = state.value.copy(
                    error = null,
                    saveSuccess = true
                )
            } catch (e: Exception) {
                Log.e("MealSaving", "Error saving", e)
                _state.value = state.value.copy(
                    error = CustomInputError.MealPlanGenerationFailed("Save failed: ${e.message}"),
                    saveSuccess = false
                )
            }
        }
    }


    private fun findMealByType(meals: List<Meal>, type: String, allowedCategories: List<String>): Meal? {
        return meals
            .filter {
                it.category.equals(type, ignoreCase = true) &&
                        allowedCategories.any { cat -> it.category.equals(cat, ignoreCase = true) }
            }
            .shuffled()
            .firstOrNull()
            ?.also { Log.d("MealGeneration", "Found $type: ${it.name} (ID: ${it.id})") }
    }
}