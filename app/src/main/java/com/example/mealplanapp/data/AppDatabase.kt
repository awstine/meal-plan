package com.example.mealplanapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mealplanapp.data.entity.Meal
import com.example.mealplanapp.data.converters.Converters
import com.example.mealplanapp.data.dao.MealPlanDao
import com.example.mealplanapp.data.entity.SavedMealPlan

@Database(
    entities = [Meal::class, SavedMealPlan::class],
    version = 1,
    exportSchema = false
)
//@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mealPlanDao(): MealPlanDao
}
