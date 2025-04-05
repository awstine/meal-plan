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
//object AppModule {
//
////    @Provides
////    @Singleton
////    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
////        return Room.databaseBuilder(
////            appContext,
////            AppDatabase::class.java,
////            "app_database"
////        )
//            // Remove the line below if it exists
//            //.addTypeConverter(Converters())
////            .build()
////    }
//
//}