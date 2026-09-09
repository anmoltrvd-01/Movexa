package com.movexa.android.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movexa.android.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun setError(message: String) {
        _error.value = message
    }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _error.value = "Please fill all fields"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            authRepository.login(email, password)
                .onSuccess {
                    onSuccess()
                }
                .onFailure {
                    _error.value = it.message ?: "Login failed"
                }
            _isLoading.value = false
        }
    }

    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            authRepository.signInWithGoogle(idToken)
                .onSuccess {
                    onSuccess()
                }
                .onFailure {
                    _error.value = it.message ?: "Google sign in failed"
                }
            _isLoading.value = false
        }
    }
}
