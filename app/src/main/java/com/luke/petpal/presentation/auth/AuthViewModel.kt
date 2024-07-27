package com.luke.petpal.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.luke.petpal.data.repository.AuthRepository
import com.luke.petpal.data.models.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
): ViewModel() {

    private val _loginFlow = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val loginFlow: StateFlow<Resource<FirebaseUser>?> = _loginFlow

    private val _signUpFlow = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val signUpFlow: StateFlow<Resource<FirebaseUser>?> = _signUpFlow

    private val _isEmailVerifiedFlow = MutableStateFlow<Boolean?>(null)
    val isEmailVerifiedFlow: StateFlow<Boolean?> = _isEmailVerifiedFlow

    private val _passwordResetFlow = MutableStateFlow<Resource<Unit>?>(null)
    val passwordResetFlow: StateFlow<Resource<Unit>?> = _passwordResetFlow

    val currentUser: FirebaseUser?
        get() = repository.currentUser

    init {
        if (repository.currentUser != null) {
            _loginFlow.value = Resource.Success(repository.currentUser!!)
        }
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        _loginFlow.value = Resource.Loading
        val result = repository.login(email, password)
        _loginFlow.value = result
    }

    fun signUp(username: String, email: String, password: String) = viewModelScope.launch {
        _signUpFlow.value = Resource.Loading
        val result = repository.signUp(username, email, password)
        _signUpFlow.value = result
    }

    private fun isEmailVerified() = viewModelScope.launch {
        _isEmailVerifiedFlow.value = try {
            repository.isEmailVerified()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun sendPasswordResetEmail(email: String) = viewModelScope.launch {
        _passwordResetFlow.value = Resource.Loading
        val result = repository.sendPasswordResetEmail(email)
        _passwordResetFlow.value = result
    }

    fun resetPasswordResetFlow() {
        _passwordResetFlow.value = null
    }

    fun logout() {
        repository.logout()
        _loginFlow.value = null
        _signUpFlow.value = null
    }
}