package com.example.mealplanapp.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//object AppModule {
//
//    @Provides
//    @Singleton
//    fun provideMealPlanDatabase(
//        @ApplicationContext context: Context
//    ) = Room.databaseBuilder(
//        context,
//        MealPlanDatabase::class.java,
//        "meal_plan_database"
//    ).build()
//
//    @Provides
//    fun provideMealPlanDao(db: MealPlanDatabase) = db.mealPlanDao()
//}