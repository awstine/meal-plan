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
        // Reset state before starting generation
        _state.value = state.value.copy(isLoading = true, generatedPlan = null, error = null, saveSuccess = false)

        viewModelScope.launch {
            try {
                // Load fresh meals each time or use preloaded ones (_allMeals.value)
                val meals: List<Meal> = mealRepository.loadMealsFromCSV() // Or get from DB
                Log.d("CustomGoalInputVM", "Loaded ${meals.size} meals for generation.")

                if (meals.isEmpty()) {
                    _state.value = state.value.copy(
                        error = CustomInputError.MealPlanGenerationFailed("No meals available in the database."),
                        isLoading = false
                    )
                    Log.e("CustomGoalInputVM", "No meals loaded from repository.")
                    return@launch
                }

                val goal = _state.value.goal.lowercase().trim()
                if (goal.isBlank()) {
                    _state.value = state.value.copy(
                        error = CustomInputError.MealPlanGenerationFailed("Please enter a health goal."),
                        isLoading = false
                    )
                    return@launch
                }


                // Define categories based on goal
                val categories = when {
                    goal.contains("lose weight") || goal.contains("weight loss") ->
                        listOf("Snack", "Beverage", "Vegetarian", "Salad", "Main Dish", "Seafood", "Breakfast", "Lunch", "Dinner", "Supper") // Be explicit
                    goal.contains("gain weight") || goal.contains("weight gain") || goal.contains("build muscle") || goal.contains("muscle gain")->
                        listOf("Bread", "Snack", "Beverage", "Main Dish", "Vegetarian", "Seafood", "Staple", "Breakfast", "Lunch", "Dinner", "Supper")
                    else -> // Default or maintenance
                        listOf("Bread", "Snack", "Beverage", "Main Dish", "Vegetarian", "Salad", "Seafood", "Staple", "Breakfast", "Lunch", "Dinner", "Supper")
                }
                Log.d("CustomGoalInputVM", "Using categories for goal '$goal': $categories")

                // Select actual Meal objects
                val breakfastSelection = selectRandomMeals(meals, categories, mealType = "Breakfast")
                val lunchSelection = selectRandomMeals(meals, categories, mealType = "Lunch")
                // Combine Supper/Dinner for flexibility
                val supperSelection = selectRandomMeals(meals, categories, mealType = "Supper") +
                        selectRandomMeals(meals, categories, mealType = "Dinner")


                if (breakfastSelection.isEmpty() || lunchSelection.isEmpty() || supperSelection.isEmpty()) {
                    Log.w("CustomGoalInputVM", "Could not find meals for all meal types. Breakfast: ${breakfastSelection.size}, Lunch: ${lunchSelection.size}, Supper/Dinner: ${supperSelection.size}")
                    // Decide if partial plan is ok or show error
                }

                // Create the details object for the UI state
                // Ensure uniqueness and take first if multiple found per type
                val planDetails = GeneratedPlanDetails(
                    breakfast = breakfastSelection.distinctBy { it.id }.take(1), // Take 1 breakfast
                    lunch = lunchSelection.distinctBy { it.id }.take(1),       // Take 1 lunch
                    supper = supperSelection.shuffled().distinctBy { it.id }.take(1) // Take 1 random supper/dinner
                )


                _state.value = state.value.copy(
                    generatedPlan = planDetails, // Store the details object
                    isLoading = false,
                    error = null
                )

                Log.i("CustomGoalInputVM", "Generated Meal Plan Details: Breakfast=${planDetails.breakfast.joinToString { it.name }}, Lunch=${planDetails.lunch.joinToString { it.name }}, Supper=${planDetails.supper.joinToString { it.name }}")

            } catch (e: Exception) {
                Log.e("CustomGoalInputVM", "Error generating meal plan", e)
                _state.value = state.value.copy(
                    error = CustomInputError.MealPlanGenerationFailed(e.message ?: "Unknown error during generation"),
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


    @RequiresApi(Build.VERSION_CODES.O) // Keep if needed for LocalDate
    fun saveCurrentMealPlan() {
        viewModelScope.launch {
            val planDetails = state.value.generatedPlan // Get the GeneratedPlanDetails object
            val breakfastMeal = planDetails?.breakfast?.firstOrNull() // Get the first (and likely only) Meal object
            val lunchMeal = planDetails?.lunch?.firstOrNull()
            val supperMeal = planDetails?.supper?.firstOrNull()

            // --- VALIDATION ---
            if (breakfastMeal == null || lunchMeal == null || supperMeal == null) {
                Log.w("CustomGoalInputVM", "Attempted to save an incomplete meal plan. Breakfast: ${breakfastMeal?.id}, Lunch: ${lunchMeal?.id}, Supper: ${supperMeal?.id}")
                _state.value = state.value.copy(
                    error = CustomInputError.MealPlanGenerationFailed("Cannot save incomplete plan. Generate again."),
                    saveSuccess = false // Ensure success is false
                )
                return@launch
            }

            val currentDate = LocalDate.now()

            // --- Use IDs directly from the Meal objects ---
            val mealToSave = SavedMealPlan(
                date = currentDate,
                breakfastId = breakfastMeal.id, // Use ID from the Meal object
                lunchId = lunchMeal.id,       // Use ID from the Meal object
                supperId = supperMeal.id        // Use ID from the Meal object
            )

            try {
                mealRepository.insertSavedMealPlan(mealToSave)
                Log.i("CustomGoalInputVM", "Saved meal plan successfully: $mealToSave")
                // Update state to indicate success, clear error
                _state.value = state.value.copy(
                    error = null,
                    saveSuccess = true // Set success flag
                )
            } catch(e: Exception) {
                Log.e("CustomGoalInputVM", "Error saving meal plan to repository", e)
                _state.value = state.value.copy(
                    error = CustomInputError.MealPlanGenerationFailed("Failed to save meal plan: ${e.message}"),
                    saveSuccess = false
                )
            }
        }
    }

    // Removed setCustomGoal and resetState as onEvent handles this
}
// Ensure MealPlan data class is defined somewhere accessible, e.g.:
// package com.example.mealplanapp.data.model
// data class MealPlan(val meals: List<Pair<String, String>>) // Example: List of (MealType, MealName(s))