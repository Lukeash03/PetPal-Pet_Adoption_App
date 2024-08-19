package com.luke.petpal.presentation

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.luke.petpal.data.models.Resource
import com.luke.petpal.data.repository.HomeRepository
import com.luke.petpal.data.repository.ProfileImageRepository
import com.luke.petpal.domain.data.Pet
import com.luke.petpal.presentation.auth.googlesignin.GoogleAuthUIClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val profileImageRepository: ProfileImageRepository,
    private val googleAuthUIClient: GoogleAuthUIClient,
) : ViewModel() {

    private val _loginFlow = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val loginFlow: StateFlow<Resource<FirebaseUser>?> = _loginFlow

    private val _uploadImageResult = MutableStateFlow<Resource<String>?>(null)
    val uploadImageResult: StateFlow<Resource<String>?> = _uploadImageResult

    private val _updateProfileImageResult = MutableStateFlow<Resource<Unit>?>(null)
    val updateProfileImageResult: StateFlow<Resource<Unit>?> = _updateProfileImageResult

    private val _profileImageUrl = MutableStateFlow<Resource<String>>(Resource.Loading)
    val profileImageUrl: StateFlow<Resource<String>> get() = _profileImageUrl

    private val _uploadStatus = MutableStateFlow<Resource<Unit>?>(null)
    val uploadStatus: StateFlow<Resource<Unit>?> = _uploadStatus

    private val _petList = MutableStateFlow<List<Pet>?>(emptyList())
    val petList: StateFlow<List<Pet>?> = _petList

    init {
        fetchPets()
    }

    val currentUser: FirebaseUser? get() = homeRepository.currentUser

    fun logout() {
        viewModelScope.launch {
            homeRepository.logout()
            googleAuthUIClient.signOut()
            _loginFlow.value = null
        }
    }

    fun uploadProfileImage(uri: Uri) = viewModelScope.launch {
        val result = profileImageRepository.uploadProfileImage(uri)
        _uploadImageResult.value = result
        Log.i("MyTag", "uploadProfileImage: ViewModel: ${_uploadImageResult.value}")
    }

    fun updateProfileImageUrl(url: String) = viewModelScope.launch {
        val result = profileImageRepository.updateProfileImageUrl(url)
        _updateProfileImageResult.value = result
        Log.i("MyTag", "updateProfileImageUrl: ViewModel: ${_updateProfileImageResult.value}")
    }

    fun fetchProfileImageUrl() {
        viewModelScope.launch {
            val result = profileImageRepository.fetchProfileUrl()
            _profileImageUrl.value = result
        }
    }

    fun uploadPet(pet: Pet) {
        viewModelScope.launch {
            val result = homeRepository.uploadPet(pet)
            _uploadStatus.value = result
        }
    }

    private fun fetchPets() {
        viewModelScope.launch {
            val result = homeRepository.fetchPetList()
            Log.i("MYTAG", result.toString())
            if (result is Resource.Success) {
                _petList.value = result.result
            } else {
                // Handle error
            }
        }
    }

}