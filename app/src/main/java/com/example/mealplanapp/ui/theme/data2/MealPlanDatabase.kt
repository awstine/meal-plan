package com.example.mealplanapp.ui.theme.data2

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mealplanapp.ui.theme.data.MealPlan

//@Database(entities = [SavedMealPlan::class], version = 1, exportSchema = false)
//abstract class MealPlanDatabase : RoomDatabase() {
//    abstract fun mealPlanDao(): MealPlanDao
//}

@Database(
    entities = [SavedMealPlan::class], // Add SavedMealPlan as an entity
    version = 1,
    exportSchema = false // Or set to true and configure schema location
)
@TypeConverters(Converters::class)
abstract class MealPlanDatabase : RoomDatabase() {
    abstract fun mealPlanDao(): MealPlanDao

    companion object {
        @Volatile
        private var INSTANCE: MealPlanDatabase? = null

        fun getDatabase(context: Context): MealPlanDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MealPlanDatabase::class.java,
                    "meal_plan_database"
                )
                    .fallbackToDestructiveMigration() // For development only
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}