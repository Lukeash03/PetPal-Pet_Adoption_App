package com.luke.petpal.presentation.auth

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.model.Place
import com.google.firebase.auth.FirebaseUser
import com.luke.petpal.data.repository.AuthRepository
import com.luke.petpal.data.models.Resource
import com.luke.petpal.domain.repository.usecase.ValidateEmail
import com.luke.petpal.domain.repository.usecase.ValidatePassword
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
    private val repository: AuthRepository,
    private val googleAuthUIClient: GoogleAuthUIClient,
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

    private val _uploadImageResult = MutableStateFlow<Resource<String>?>(null)
    val uploadImageResult: StateFlow<Resource<String>?> = _uploadImageResult

    private val _updateProfileImageResult = MutableStateFlow<Resource<Unit>?>(null)
    val updateProfileImageResult: StateFlow<Resource<Unit>?> = _updateProfileImageResult

    private val _location = MutableStateFlow<Place?>(null)
    val location: StateFlow<Place?> = _location

    var state by mutableStateOf(RegistrationFormState())

    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

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

    fun onEvent(event: RegistrationFormEvent) {
        when(event) {
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
        val emailResult = validateEmail.execute(state.email)
        val passwordResult = validatePassword.execute(state.password)

        val hasError = listOf(
            emailResult,
            passwordResult
        ).any() { !it.successful }

        if (hasError) {
            state = state.copy(
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
        val result = repository.sendPasswordResetEmail(email)
        _passwordResetFlow.value = result
    }

    fun resetPasswordResetFlow() {
        _passwordResetFlow.value = null
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            googleAuthUIClient.signOut()
            _loginFlow.value = null
            _signUpFlow.value = null
        }
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

    fun uploadProfileImage(uri: Uri) = viewModelScope.launch {
        val result = repository.uploadProfileImage(uri)
        _uploadImageResult.value = result
    }

    fun updateProfileImageUrl(url: String) = viewModelScope.launch {
        val result = repository.updateProfileImageUrl(url)
        _updateProfileImageResult.value = result
    }

    fun updateLocation(place: Place) {
        _location.value = place
    }

    sealed class ValidationEvent {
        data object Success: ValidationEvent()
    }
}