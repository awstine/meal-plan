package com.example.mealplanapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mealplanapp.data.entity.Meal
import com.example.mealplanapp.data.entity.SavedMealPlan
import kotlinx.coroutines.flow.Flow

//@Dao
//interface MealPlanDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertMeal(meal: Meal)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertSavedMealPlan(mealPlan: SavedMealPlan)
//
//    @Delete
//    suspend fun deleteSavedMealPlan(mealPlan: SavedMealPlan)
//
//    @Query("SELECT * FROM saved_meal_plan ORDER BY date DESC")
//    fun getAllSavedMealPlans(): Flow<List<SavedMealPlan>>
//
//    @Query("SELECT * FROM meals WHERE id IN (:mealIds)")
//    fun getMealsByIds(mealIds: List<Int>): Flow<List<Meal>>
//
//    @Query("SELECT * FROM meals")
//    fun getAllMeals(): Flow<List<Meal>>
//
//    @Transaction
//    @Query("SELECT * FROM saved_meal_plan WHERE id = :mealPlanId")
//    fun getMealPlanWithMeals(mealPlanId: Int): Flow<MealPlanWithMeals>
//
//    @Query("SELECT COUNT(*) FROM saved_meal_plan")
//    suspend fun getSavedCount(): Int
//
//    @Query("SELECT * FROM meals WHERE name = :name")
//    suspend fun getMealByName(name: String): Meal?
//}


@Dao
interface MealPlanDao {
    // Meal operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: Meal)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeals(meals: List<Meal>) // Add this if you bulk insert

    @Query("SELECT * FROM meals WHERE id = :mealId")
    suspend fun getMealById(mealId: Int): Meal? // Make suspend and nullable

    @Query("SELECT * FROM meals WHERE id IN (:mealIds)")
    fun getMealsByIds(mealIds: List<Int>): Flow<List<Meal>> // Keep this

    @Query("SELECT * FROM meals")
    fun getAllMeals(): Flow<List<Meal>>

    @Query("SELECT * FROM meals WHERE name = :name")
    suspend fun getMealByName(name: String): Meal?

    // SavedMealPlan operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedMealPlan(mealPlan: SavedMealPlan)

    @Delete
    suspend fun deleteSavedMealPlan(mealPlan: SavedMealPlan)

    @Query("SELECT * FROM saved_meal_plan ORDER BY date DESC")
    fun getAllSavedMealPlans(): Flow<List<SavedMealPlan>> // Changed return type

    @Query("SELECT COUNT(*) FROM saved_meal_plan")
    suspend fun getSavedCount(): Int

    // Remove unused or incorrect queries/relations
    // @Transaction
    // @Query("SELECT * FROM saved_meal_plan WHERE id = :mealPlanId")
    // fun getMealPlanWithMeals(mealPlanId: Int): Flow<MealPlanWithMeals> // Remove this
}