package com.luke.petpal.presentation

import android.content.Context
import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.luke.petpal.data.models.Resource
import com.luke.petpal.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
) : ViewModel() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val _uploadImageResult = MutableStateFlow<Resource<String>?>(null)
    val uploadImageResult: StateFlow<Resource<String>?> = _uploadImageResult

    private val _updateProfileImageResult = MutableStateFlow<Resource<Unit>?>(null)
    val updateProfileImageResult: StateFlow<Resource<Unit>?> = _updateProfileImageResult

    private val _profileImageUrl = MutableStateFlow<Resource<String>>(Resource.Loading)
    val profileImageUrl: StateFlow<Resource<String>> get() = _profileImageUrl

    private val _locationFlow = MutableStateFlow<Location?>(null)
    val locationFlow: StateFlow<Location?> = _locationFlow

    private val _updateLocationResult = MutableStateFlow<Resource<Unit>?>(null)
    val updateLocationResult: StateFlow<Resource<Unit>?> = _updateLocationResult

    private val _uploadState = MutableStateFlow<Resource<Unit>?>(null)
    val uploadState: StateFlow<Resource<Unit>?> = _uploadState

    fun uploadProfileImage(uri: Uri) = viewModelScope.launch {
        val result = userProfileRepository.uploadProfileImage(uri)
        _uploadImageResult.value = result
        Log.i("MyTag", "uploadProfileImage: ViewModel: ${_uploadImageResult.value}")
    }

    fun updateProfileImageUrl(url: String) = viewModelScope.launch {
        val result = userProfileRepository.updateProfileImageUrl(url)
        _updateProfileImageResult.value = result
        Log.i("MyTag", "updateProfileImageUrl: ViewModel: ${_updateProfileImageResult.value}")
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

    fun updateLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _updateLocationResult.value = userProfileRepository.updateLocation(latitude, longitude)
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
