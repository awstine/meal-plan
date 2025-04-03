package com.example.mealplanapp.ui.theme.data2

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mealplanapp.ui.theme.data.Meal
import com.example.mealplanapp.ui.theme.data.MealPlan
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Database(entities = [SavedMealPlan::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mealPlanDao(): MealPlanDao

//    companion object {
//        @Volatile
//        private var INSTANCE: AppDatabase? = null
//
//        fun getDatabase(context: Context): AppDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    AppDatabase::class.java,
//                    "meal_plan_database"
//                ).build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
}

//@Module
//@InstallIn(SingletonComponent::class)
//object DatabaseModule {
//    @Singleton
//    @Provides
//    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
//        return Room.databaseBuilder(
//            context,
//            AppDatabase::class.java,
//            "meal_plan_database"
//        ).build()
//    }
//    @Provides
//    fun provideMealPlanDao(db: AppDatabase) = db.mealPlanDao()
//}