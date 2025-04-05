package com.example.mealplanapp.di

import android.content.Context
import androidx.room.Room
import com.example.mealplanapp.data.AppDatabase
import com.example.mealplanapp.data.converters.Converters
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//object DatabaseModule {
//
//    @Provides
//    @Singleton
//    fun provideConverters(): Converters {
//        return Converters()
//    }
//
//    @Provides
//    @Singleton
//    fun provideAppDatabase(
//        @ApplicationContext appContext: Context,
//        converters: Converters
//    ): AppDatabase {
//        return Room.databaseBuilder(
//            appContext,
//            AppDatabase::class.java,
//            "meal_plan_database"
//        )
//            .addTypeConverter(converters) // Provide converters here
//            .fallbackToDestructiveMigration()
//            .build()
//    }
//
//    @Provides
//    fun provideMealPlanDao(db: AppDatabase) = db.mealPlanDao()
//}
