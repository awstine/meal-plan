package com.example.mealplanapp.di

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.mealplanapp.data.AppDatabase
import com.example.mealplanapp.data.converters.Converters
import com.example.mealplanapp.data.dao.MealPlanDao // Corrected import if needed
import com.example.mealplanapp.data.repository.MealRepository
import com.example.mealplanapp.ui.theme.screen.health.AuthViewModel
import com.example.mealplanapp.ui.theme.screen.health.HealthConditionViewModel
// Import the CustomGoalInputViewModel
import com.example.mealplanapp.ui.theme.screen.health.goalinput.CustomGoalInputViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

object DependencyContainer {
    private lateinit var application: Application

    // Firebase lazy initializations remain the same
    private val firebaseAuth: FirebaseAuth by lazy { Firebase.auth }
    private val firestore: FirebaseFirestore by lazy { Firebase.firestore }

    // Room Database and DAO lazy initializations remain the same
    val appDatabase: AppDatabase by lazy {
        checkInitialized() // Add check
        Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "meal_plan_database"
        )
            .addTypeConverter(Converters()) // Ensure Converters class exists and is needed
            .fallbackToDestructiveMigration() // Consider proper migrations for production
            .build()
    }

    val mealPlanDao: MealPlanDao by lazy { // Ensure MealPlanDao is the correct name
        appDatabase.mealPlanDao() // Ensure method name is correct in AppDatabase
    }

    // MealRepository lazy initialization remains the same
    val mealRepository: MealRepository by lazy {
        checkInitialized() // Add check
        MealRepository(
            mealPlanDao = mealPlanDao,
            appContext = application.applicationContext // Pass context
        )
    }

    fun init(app: Application) { // Renamed parameter for clarity
        this.application = app
    }

    // Helper to ensure init() was called
    private fun checkInitialized() {
        if (!this::application.isInitialized) {
            throw UninitializedPropertyAccessException("DependencyContainer has not been initialized. Call init() in your Application class.")
        }
    }


    // Factory for AuthViewModel remains the same
    fun provideAuthViewModelFactory(): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(
                    auth = firebaseAuth,
                    firestore = firestore
                ) as T
            }
        }
    }

    // Factory for HealthConditionViewModel remains the same
    fun provideHealthConditionViewModelFactory(): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                // Ensure savedMealPlanDao parameter name matches the ViewModel constructor
                return HealthConditionViewModel(
                    mealRepository = mealRepository,
                    savedMealPlanDao = mealPlanDao
                ) as T
            }
        }
    }

    // --- ADD THIS FACTORY for CustomGoalInputViewModel ---
    fun provideCustomGoalInputViewModelFactory(): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                // Check if the requested ViewModel is CustomGoalInputViewModel
                if (modelClass.isAssignableFrom(CustomGoalInputViewModel::class.java)) {
                    // Create it using the mealRepository from this container
                    return CustomGoalInputViewModel(
                        mealRepository = mealRepository
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}