package com.example.mealplanapp.ui.theme.data2

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MealPlanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mealPlan: SavedMealPlan)

    @Delete
    suspend fun delete(mealPlan: SavedMealPlan)

    @Query("SELECT * FROM saved_meal_plans ORDER BY date DESC")
    fun getAllSavedMealPlans(): Flow<List<SavedMealPlan>>
}