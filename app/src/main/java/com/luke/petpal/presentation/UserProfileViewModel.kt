package com.luke.petpal.presentation

import android.content.Context
import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.auth.FirebaseUser
import com.luke.petpal.data.models.Resource
import com.luke.petpal.data.repository.HomeRepository
import com.luke.petpal.data.repository.UserProfileRepository
import com.luke.petpal.domain.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val homeRepository: HomeRepository
) : ViewModel() {

    val currentUser: FirebaseUser? get() = userProfileRepository.currentUser

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val _userById = MutableStateFlow<User?>(null)
    val userById: StateFlow<User?> = _userById

    private val _profileImageUrl = MutableStateFlow<Resource<String>>(Resource.Loading)
    val profileImageUrl: StateFlow<Resource<String>> get() = _profileImageUrl

    private val _locationFlow = MutableStateFlow<Location?>(null)
    val locationFlow: StateFlow<Location?> = _locationFlow

    private val _uploadState = MutableStateFlow<Resource<Unit>?>(null)
    val uploadState: StateFlow<Resource<Unit>?> = _uploadState

    fun fetchUserById(userId: String?) {
        viewModelScope.launch {
            val result = userId?.let { homeRepository.fetchUserById(it) }
            if (result is Resource.Success) {
                _userById.value = result.result
            }
        }
    }

    fun fetchProfileImageUrl() {
        viewModelScope.launch {
            val result = userProfileRepository.fetchProfileUrl()
            _profileImageUrl.value = result
        }
    }

    fun getLocation(context: Context) {
        viewModelScope.launch {
            userProfileRepository.fetchCurrentLocation(context) { location ->
                _locationFlow.value = location
            }
        }
    }

    fun uploadProfileImageAndLocation(imageUri: Uri?, location: Location?) {
        viewModelScope.launch {
            try {
                _uploadState.value = Resource.Loading

                imageUri?.let { uri ->
                    val uploadResult = userProfileRepository.uploadProfileImage(uri)
                    if (uploadResult is Resource.Success) {
                        val imageUrl = uploadResult.result
                        userProfileRepository.updateProfileImageUrl(imageUrl)
                    }
                }

                location?.let {
                    userProfileRepository.updateLocation(it.latitude, it.longitude)
                }

                _uploadState.value = Resource.Success(Unit)
            } catch (e: Exception) {
                _uploadState.value = Resource.Failure(e)
            }
        }
    }

}
