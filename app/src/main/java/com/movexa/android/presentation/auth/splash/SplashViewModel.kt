package com.movexa.android.presentation.auth.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movexa.android.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState?>(null)
    val authState: StateFlow<AuthState?> = _authState.asStateFlow()

    init {
        checkAuth()
    }

    private fun checkAuth() {
        viewModelScope.launch {
            val user = authRepository.getSession().first()
            val hasSeenOnboarding = authRepository.hasSeenOnboarding().first()

            _authState.value = when {
                user != null -> AuthState.Authenticated
                hasSeenOnboarding -> AuthState.NotAuthenticated
                else -> AuthState.FirstTime
            }
        }
    }

    sealed class AuthState {
        object Authenticated : AuthState()
        object NotAuthenticated : AuthState()
        object FirstTime : AuthState()
    }
}