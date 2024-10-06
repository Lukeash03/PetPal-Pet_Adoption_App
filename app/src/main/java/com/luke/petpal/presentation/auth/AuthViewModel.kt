package com.luke.petpal.presentation.auth

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.model.Place
import com.google.firebase.auth.FirebaseUser
import com.luke.petpal.data.repository.AuthRepository
import com.luke.petpal.data.models.Resource
import com.luke.petpal.data.repository.UserProfileRepository
import com.luke.petpal.domain.repository.usecase.ValidateEmail
import com.luke.petpal.domain.repository.usecase.ValidatePassword
import com.luke.petpal.domain.repository.usecase.ValidateUsername
import com.luke.petpal.presentation.auth.googlesignin.GoogleAuthUIClient
import com.luke.petpal.presentation.auth.googlesignin.SignInResult
import com.luke.petpal.presentation.auth.googlesignin.SignInState
import com.luke.petpal.presentation.auth.validation.RegistrationFormEvent
import com.luke.petpal.presentation.auth.validation.RegistrationFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val googleAuthUIClient: GoogleAuthUIClient,
    private val validateUsername: ValidateUsername = ValidateUsername(),
    private val validateEmail: ValidateEmail = ValidateEmail(),
    private val validatePassword: ValidatePassword = ValidatePassword()
) : ViewModel() {

    private val _loginFlow = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val loginFlow: StateFlow<Resource<FirebaseUser>?> = _loginFlow

    private val _signUpFlow = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val signUpFlow: StateFlow<Resource<FirebaseUser>?> = _signUpFlow

    private val _passwordResetFlow = MutableStateFlow<Resource<Unit>?>(null)
    val passwordResetFlow: StateFlow<Resource<Unit>?> = _passwordResetFlow

    private val _googleSignInFlow = MutableStateFlow(SignInState())
    val googleSignInFlow = _googleSignInFlow.asStateFlow()

    var state by mutableStateOf(RegistrationFormState())

    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    val currentUser: FirebaseUser?
        get() = authRepository.currentUser

    init {
        if (authRepository.currentUser != null) {
            Log.i("MyTag", "User: $currentUser is logged in")
            _loginFlow.value = Resource.Success(authRepository.currentUser!!)
        }
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        _loginFlow.value = Resource.Loading
        val result = authRepository.login(email, password)
        _loginFlow.value = result
    }

    fun signUp(username: String, email: String, password: String) = viewModelScope.launch {
        _signUpFlow.value = Resource.Loading
        val result = authRepository.signUp(username, email, password)
        _signUpFlow.value = result
    }

    fun onEvent(event: RegistrationFormEvent) {
        when(event) {

            is RegistrationFormEvent.UsernameChanged -> {
                state = state.copy(username = event.username)
            }
            is RegistrationFormEvent.EmailChanged -> {
                state = state.copy(email = event.email)
            }
            is RegistrationFormEvent.PasswordChanged -> {
                state = state.copy(password = event.password)
            }
            is RegistrationFormEvent.Submit -> {
                submitData()
            }

        }
    }

    private fun submitData() {
        val usernameResult = validateUsername.execute(state.username)
        val emailResult = validateEmail.execute(state.email)
        val passwordResult = validatePassword.execute(state.password)

        val hasError = listOf(
            usernameResult,
            emailResult,
            passwordResult
        ).any { !it.successful }

        if (hasError) {
            state = state.copy(
                usernameError = usernameResult.errorMessage,
                emailError = emailResult.errorMessage,
                passwordError = passwordResult.errorMessage
            )
            return
        }

        viewModelScope.launch {
            validationEventChannel.send(ValidationEvent.Success)
        }
    }

//    private fun isEmailVerified() = viewModelScope.launch {
//        _isEmailVerifiedFlow.value = try {
//            repository.isEmailVerified()
//        } catch (e: Exception) {
//            e.printStackTrace()
//            false
//        }
//    }

    fun sendPasswordResetEmail(email: String) = viewModelScope.launch {
        _passwordResetFlow.value = Resource.Loading
        val result = authRepository.sendPasswordResetEmail(email)
        _passwordResetFlow.value = result
    }

    fun resetPasswordResetFlow() {
        _passwordResetFlow.value = null
    }

    private fun onSignInResult(result: SignInResult) {
        _googleSignInFlow.update {
            it.copy(
                isSignInSuccessful = result.data != null,
                signInError = result.errorMessage
            )
        }
    }

    fun resetState() {
        _googleSignInFlow.update { SignInState() }
    }

    fun triggerGoogleSignIn() = viewModelScope.launch {
        val signInIntentSender = googleAuthUIClient.signIn()
        _googleSignInFlow.update { it.copy(signInIntent = signInIntentSender) }
    }

    fun signInWithGoogle(intent: Intent) = viewModelScope.launch {
        val result = googleAuthUIClient.signInWithIntent(intent)
        onSignInResult(result)
    }

    sealed class ValidationEvent {
        data object Success: ValidationEvent()
    }
}