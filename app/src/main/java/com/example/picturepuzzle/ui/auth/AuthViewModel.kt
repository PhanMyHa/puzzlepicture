package com.example.picturepuzzle.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.picturepuzzle.data.firebase.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    fun register(email: String, password: String) = viewModelScope.launch {
        _authState.value = AuthState.Loading
        try {
            val user = authRepo.register(email, password)
            _authState.value = AuthState.Success(user?.uid ?: "")
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Register failed")
        }
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        _authState.value = AuthState.Loading
        try {
            val user = authRepo.login(email, password)
            _authState.value = AuthState.Success(user?.uid ?: "")
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Login failed")
        }
    }

    fun loginAnonymous() = viewModelScope.launch {
        _authState.value = AuthState.Loading
        try {
            val user = authRepo.loginAnonymous()
            _authState.value = AuthState.Success(user?.uid ?: "")
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Anonymous login failed")
        }
    }
}

sealed class AuthState {
    object Loading : AuthState()
    data class Success(val uid: String) : AuthState()
    data class Error(val message: String) : AuthState()
}