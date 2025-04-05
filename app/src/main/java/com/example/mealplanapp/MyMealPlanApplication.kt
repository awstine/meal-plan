package com.example.mealplanapp

import android.app.Application
import androidx.room.Room
import com.example.mealplanapp.data.AppDatabase
import com.example.mealplanapp.data.converters.Converters
import com.example.mealplanapp.di.DependencyContainer
import dagger.hilt.android.HiltAndroidApp


class MyMealPlanApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DependencyContainer.init(this) // Corrected
    }
}