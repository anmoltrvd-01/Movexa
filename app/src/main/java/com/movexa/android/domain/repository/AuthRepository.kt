package com.movexa.android.domain.repository

import com.movexa.android.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getSession(): Flow<User?>
    fun hasSeenOnboarding(): Flow<Boolean>
    suspend fun setOnboardingCompleted()
    suspend fun login(email: String, password: String): Result<User>
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun signUp(
        name: String, 
        email: String, 
        password: String,
        age: Int? = null,
        weight: Float? = null,
        height: Float? = null,
        goal: String? = null
    ): Result<User>
    suspend fun logout()
    suspend fun sendOtp(email: String): Result<Unit>
    suspend fun verifyOtp(email: String, otp: String): Result<User>
}