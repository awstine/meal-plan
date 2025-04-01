package com.example.mealplanapp.ui.theme.data2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MealViewModel : ViewModel() {
    private val _meals = MutableStateFlow<List<Meal>>(emptyList())
    val meals: StateFlow<List<Meal>> = _meals

    private val _selectedMeal = MutableStateFlow<Meal?>(null)
    val selectedMeal: StateFlow<Meal?> = _selectedMeal

    private val _mealPlan = MutableStateFlow<Map<String, List<Meal>>?>(null)
    val mealPlan: StateFlow<Map<String, List<Meal>>?> = _mealPlan

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadMealsByCategory(category: String) {
        _meals.value = LocalMealDataSource.getMealsByCategory(category)
    }

    fun loadMealById(id: Int) {
        _selectedMeal.value = LocalMealDataSource.getMealById(id)
    }

    fun getCategories(): List<String> {
        return LocalMealDataSource.getAllCategories()
    }

    fun getMealById(mealId: Int): Meal? {
        return meals.value.find { it.id == mealId }
    }

    fun generateMealPlan(userCondition: String) {
        viewModelScope.launch {
            _isLoading.value = true // Start loading
            delay(5000) // Simulate processing

            val categories = LocalMealDataSource.getAllCategories()
            val randomizedMealPlan = categories.associateWith { category ->
                LocalMealDataSource.getMealsByCategory(category).shuffled().take(1)
            }

            _mealPlan.value = randomizedMealPlan
            _isLoading.value = false // Stop loading
        }
    }

}
