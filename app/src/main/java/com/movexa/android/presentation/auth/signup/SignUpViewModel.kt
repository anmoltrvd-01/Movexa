package com.movexa.android.presentation.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movexa.android.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun signUp(
        name: String, 
        email: String, 
        password: String, 
        age: String,
        weight: String,
        height: String,
        goal: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val ageInt = age.toIntOrNull()
            val weightFloat = weight.toFloatOrNull()
            val heightFloat = height.toFloatOrNull()

            authRepository.signUp(name, email, password, ageInt, weightFloat, heightFloat, goal)
                .onSuccess {
                    onSuccess()
                }
                .onFailure {
                    _error.value = it.message ?: "Sign up failed"
                }
            _isLoading.value = false
        }
    }
}