package com.example.mealplanapp.ui.theme.screen.health

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.isEmpty
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplanapp.data.LocalMealDataSource
import com.example.mealplanapp.data.dao.MealPlanDao
import com.example.mealplanapp.data.entity.Meal
import com.example.mealplanapp.data.entity.MealPlanDetails
import com.example.mealplanapp.data.entity.SavedMealPlan
import com.example.mealplanapp.data.repository.MealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject


class HealthConditionViewModel(
    private val mealRepository: MealRepository,
    private val savedMealPlanDao: MealPlanDao
) : ViewModel() {
    // --- States for Meals ---
    private val _meals = mutableStateOf<List<Meal>>(emptyList())
    val meals: State<List<Meal>> = _meals

    // --- State for the list of all available meals ---
    private val _allMeals = MutableStateFlow<List<Meal>>(emptyList())
    val allMeals: StateFlow<List<Meal>> = _allMeals.asStateFlow()

    // --- States for the CURRENTLY GENERATED meal plan ---
    private val _currentBreakfast = mutableStateOf<Meal?>(null)
    val currentBreakfast: State<Meal?> = _currentBreakfast

    private val _currentLunch = mutableStateOf<Meal?>(null)
    val currentLunch: State<Meal?> = _currentLunch

    private val _currentSupper = mutableStateOf<Meal?>(null)
    val currentSupper: State<Meal?> = _currentSupper

    // --- State for saved meal plans (for SavedMealsScreen) ---
    val allSavedMealPlansWithDetails: StateFlow<List<MealPlanDetails>> =
        savedMealPlanDao.getAllSavedMealPlans()
            .flatMapLatest { savedPlans ->
                if (savedPlans.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    val mealIds =
                        savedPlans.flatMap { listOf(it.breakfastId, it.lunchId, it.supperId) }
                            .distinct()
                    savedMealPlanDao.getMealsByIds(mealIds)
                        .map { meals ->
                            val mealsById = meals.associateBy { it.id }
                            savedPlans.map { plan ->
                                MealPlanDetails(
                                    savedMealPlan = plan,
                                    breakfast = mealsById[plan.breakfastId],
                                    lunch = mealsById[plan.lunchId],
                                    supper = mealsById[plan.supperId]
                                )
                            }
                        }
                }
            }
            .catch { e ->
                Log.e("ViewModel", "Error fetching saved meal plans with details", e)
                emit(emptyList())
                _toastMessage.value = "Error loading saved plans"
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // --- Other States ---
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _toastMessage = mutableStateOf<String?>(null)
    val toastMessage: State<String?> = _toastMessage



    init {
        // Load all meals once when ViewModel is created
        loadAllMeals()
    }

    private fun loadAllMeals() {
        viewModelScope.launch {
            // Check if DB is empty before trying to insert defaults
            val count = mealRepository.getMealCount() // Assumes this method exists in repo/DAO
            Log.d("ViewModel", "Current meal count in DB: $count")
            if (count == 0) {
                Log.d("ViewModel", "Database is empty, attempting to insert default meals from CSV.")
                insertDefaultMeals() // Load from CSV if empty
            } else {
                Log.d("ViewModel", "Database already contains meals, skipping default insertion.")
            }

            // Collect all meals from the DB into the state flow
            Log.d("ViewModel", "Starting to collect all meals from repository.")
            mealRepository.getAllMeals().collect { meals ->
                _allMeals.value = meals
                Log.d("ViewModel", "Collected ${meals.size} meals into _allMeals StateFlow.")
                // Optionally generate an initial plan once meals are loaded *and* no plan exists
                if (_currentBreakfast.value == null && _currentLunch.value == null && _currentSupper.value == null && meals.isNotEmpty()) {
                    Log.d("ViewModel", "Generating initial random meal plan.")
                    generateNewMealPlan() // The default random one
                }
            }
        }
    }

    private suspend fun insertDefaultMeals() {
        val mealsFromCsv = mealRepository.loadMealsFromCSV()
        if (mealsFromCsv.isNotEmpty()) {
            Log.d("ViewModel", "Inserting ${mealsFromCsv.size} meals from CSV into database.")
            // Ensure you have an insertAll method in your DAO and Repository for efficiency
            mealRepository.insertMeals(mealsFromCsv)
        } else {
            // --- Fallback Removed ---
            Log.w("ViewModel", "CSV file was empty or failed to load. No default meals inserted from LocalMealDataSource.")
            // val mealsFromLocal = LocalMealDataSource.kenyanMeals
            // if (mealsFromLocal.isNotEmpty()) {
            //    Log.d("ViewModel", "Inserting ${mealsFromLocal.size} meals from LocalMealDataSource as fallback.")
            //    mealRepository.insertMeals(mealsFromLocal)
            // }
        }
    }
    // --- Functions for CURRENT plan ---

    fun generateNewMealPlan() { // This generates the RANDOM plan shown on MealPlanScreen initially
        viewModelScope.launch {
            val meals = _allMeals.value // Use meals loaded from DB
            if (meals.isEmpty()) {
                _toastMessage.value = "No meals available in database."
                Log.w("ViewModel", "Cannot generate random plan, _allMeals is empty.")
                return@launch
            }
            Log.d("ViewModel", "Generating random plan from ${meals.size} available meals.")

            // Filter meals loaded FROM THE DATABASE (which originated from CSV)
            val breakfastOptions = meals.filter { it.category.equals("Breakfast", ignoreCase = true) }
            // Add more specific categories if your CSV uses them for Lunch/Supper
            val lunchOptions = meals.filter { it.category.equals("Lunch", ignoreCase = true) || it.category.equals("Main Dish", ignoreCase = true) || it.category.equals("Staple", ignoreCase = true) || it.category.equals("Seafood", ignoreCase = true) || it.category.equals("Vegetarian", ignoreCase = true)}
            val supperOptions = meals.filter { it.category.equals("Supper", ignoreCase = true) || it.category.equals("Dinner", ignoreCase = true) || it.category.equals("Main Dish", ignoreCase = true) || it.category.equals("Staple", ignoreCase = true) || it.category.equals("Seafood", ignoreCase = true) || it.category.equals("Vegetarian", ignoreCase = true)}

            _currentBreakfast.value = if (breakfastOptions.isNotEmpty()) breakfastOptions.random() else null
            _currentLunch.value = if (lunchOptions.isNotEmpty()) lunchOptions.random() else null
            _currentSupper.value = if (supperOptions.isNotEmpty()) supperOptions.random() else null

            if (_currentBreakfast.value == null || _currentLunch.value == null || _currentSupper.value == null) {
                _toastMessage.value =
                    "Could not generate a complete meal plan (missing categories?)."
            }
        }
    }

    // Updated save function - takes no parameters, uses current state
    fun saveCurrentMealPlan() {
        val breakfast = _currentBreakfast.value
        val lunch = _currentLunch.value
        val supper = _currentSupper.value

        // Ensure all parts of the plan exist before saving
        if (breakfast == null || lunch == null || supper == null) {
            _toastMessage.value = "Cannot save incomplete meal plan."
            return
        }

        viewModelScope.launch {
            try {
                val savedMealPlan = SavedMealPlan(
                    breakfastId = breakfast.id, // Use ID from the current Meal object
                    lunchId = lunch.id,       // Use ID from the current Meal object
                    supperId = supper.id,       // Use ID from the current Meal object
                    date = LocalDate.now(),
                    goal = "Your health goal here" // Replace with actual goal if needed
                )
                savedMealPlanDao.insertSavedMealPlan(savedMealPlan)
                _toastMessage.value = "Meal plan saved successfully!"
            } catch (e: Exception) {
                Log.e("Saving", "Error saving meal plan", e)
                _toastMessage.value = "Error saving meal plan: ${e.message}"
            }
        }
    }


    // --- Functions for SAVED plans (Deletion) ---
    fun deleteMealPlan(savedMealPlan: SavedMealPlan) {
        viewModelScope.launch {
            try {
                savedMealPlanDao.deleteSavedMealPlan(savedMealPlan)
                _toastMessage.value = "Meal plan deleted."
            } catch (e: Exception) {
                Log.e("Deleting", "Error deleting meal plan", e)
                _toastMessage.value = "Error deleting meal plan: ${e.message}"
            }
        }

        fun clearToast() {
            _toastMessage.value = null
        }
        }
}