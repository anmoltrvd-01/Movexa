package com.movexa.android.presentation.auth.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movexa.android.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _isSent = MutableStateFlow(false)
    val isSent = _isSent.asStateFlow()

    fun sendOtp(email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            authRepository.sendOtp(email)
                .onSuccess {
                    _isSent.value = true
                }
                .onFailure {
                    _error.value = it.message ?: "Failed to send OTP"
                }
            _isLoading.value = false
        }
    }

    fun verifyOtp(email: String, otp: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            authRepository.verifyOtp(email, otp)
                .onSuccess {
                    onSuccess()
                }
                .onFailure {
                    _error.value = it.message ?: "Invalid OTP"
                }
            _isLoading.value = false
        }
    }
}