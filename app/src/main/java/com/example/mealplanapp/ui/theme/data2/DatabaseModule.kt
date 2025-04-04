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
    fun provideConverter(): Converters {
        return Converters()
    }

    @Provides
    @Singleton
    fun provideMealPlanDatabase(
        @ApplicationContext app: Context, converter: Converters
    ): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "meal_plan_database"
        )
            .addTypeConverter(converter)
            .build()
    }

    @Provides
    fun provideMealPlanDao(db: AppDatabase): MealPlanDao {
        return db.getMealPlanDao()
    }
}