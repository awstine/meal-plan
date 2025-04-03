package com.example.mealplanapp.ui.theme.data2



import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideMealPlanDatabase(
        @ApplicationContext context: Context
    ): MealPlanDatabase {
        return MealPlanDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideMealPlanDao(database: MealPlanDatabase): MealPlanDao {
        return database.mealPlanDao()
    }
}