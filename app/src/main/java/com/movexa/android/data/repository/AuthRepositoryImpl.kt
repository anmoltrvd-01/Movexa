package com.movexa.android.data.repository

import com.movexa.android.data.local.UserPreferences
import com.movexa.android.domain.model.User
import com.movexa.android.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import javax.inject.Inject
import javax.inject.Singleton

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

@OptIn(DelicateCoroutinesApi::class)
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val userPreferences: UserPreferences
) : AuthRepository {

    override fun getSession(): Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                val user = firebaseUser.toDomainUser()
                trySend(user)
            } else {
                trySend(null)
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.onStart {
        userPreferences.userData.first()?.let { emit(it) }
        emit(auth.currentUser?.toDomainUser())
    }

    override fun hasSeenOnboarding(): Flow<Boolean> = userPreferences.hasSeenOnboarding

    override suspend fun setOnboardingCompleted() {
        userPreferences.setHasSeenOnboarding(true)
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user!!.toDomainUser()
            userPreferences.saveUser(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(mapFirebaseException(e))
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user!!.toDomainUser()
            userPreferences.saveUser(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(mapFirebaseException(e))
        }
    }

    override suspend fun signUp(
        name: String, 
        email: String, 
        password: String,
        age: Int?,
        weight: Float?,
        height: Float?,
        goal: String?
    ): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user!!

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            firebaseUser.updateProfile(profileUpdates).await()

            val user = firebaseUser.toDomainUser().copy(
                name = name,
                age = age,
                weightKg = weight,
                heightCm = height,
                goal = goal
            )
            userPreferences.saveUser(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(mapFirebaseException(e))
        }
    }

    override suspend fun logout() {
        auth.signOut()
        userPreferences.clear()
    }

    override suspend fun sendOtp(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(mapFirebaseException(e))
        }
    }

    override suspend fun verifyOtp(email: String, otp: String): Result<User> {
        return Result.failure(Exception("Direct OTP verification is not supported in this Firebase flow. Please use the reset link sent to your email."))
    }

    private fun mapFirebaseException(e: Exception): Exception {
        val message = when (e) {
            is FirebaseAuthInvalidUserException -> "No account found with this email."
            is FirebaseAuthInvalidCredentialsException -> "Incorrect password or invalid email format."
            is FirebaseAuthUserCollisionException -> "An account already exists with this email."
            is FirebaseNetworkException -> "Network error. Please check your internet connection."
            else -> e.localizedMessage ?: "An unexpected error occurred. Please try again."
        }
        return Exception(message)
    }

    private fun FirebaseUser.toDomainUser(): User {
        return User(
            id = uid,
            email = email ?: "",
            name = displayName ?: email?.substringBefore("@") ?: "User",
            token = null
        )
    }
}
