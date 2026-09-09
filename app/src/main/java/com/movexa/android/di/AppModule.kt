package com.movexa.android.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.movexa.android.data.health.HealthConnectManager
import com.movexa.android.data.local.UserPreferences
import com.movexa.android.data.local.dao.WorkoutDao
import com.movexa.android.data.repository.AuthRepositoryImpl
import com.movexa.android.data.repository.HealthRepositoryImpl
import com.movexa.android.data.repository.WorkoutRepository
import com.movexa.android.domain.repository.AuthRepository
import com.movexa.android.domain.repository.HealthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        userPreferences: UserPreferences
    ): AuthRepository {
        return AuthRepositoryImpl(firebaseAuth, userPreferences)
    }

    @Provides
    @Singleton
    fun provideWorkoutRepository(workoutDao: WorkoutDao): WorkoutRepository {
        return WorkoutRepository(workoutDao)
    }

    @Provides
    @Singleton
    fun provideHealthConnectManager(@ApplicationContext context: Context): HealthConnectManager {
        return HealthConnectManager(context)
    }

    @Provides
    @Singleton
    fun provideHealthRepository(
        healthConnectManager: HealthConnectManager
    ): HealthRepository {
        return HealthRepositoryImpl(healthConnectManager)
    }
}