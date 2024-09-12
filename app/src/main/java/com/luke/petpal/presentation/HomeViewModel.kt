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
import com.luke.petpal.domain.data.User
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

    private val _selectedSpecies = MutableStateFlow<String?>(null)
    val selectedSpecies: StateFlow<String?> = _selectedSpecies

    private val _petByIdFlow = MutableStateFlow<Resource<Pet>?>(null)
    val petById: StateFlow<Resource<Pet>?> = _petByIdFlow

    private val _userById = MutableStateFlow<User?>(null)
    val userById: StateFlow<User?> = _userById

    private val _isLiked = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> = _isLiked

    private val _likeStatus = MutableStateFlow<Resource<Unit>?>(null)
    val likeStatus: StateFlow<Resource<Unit>?> = _likeStatus

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

    fun fetchPets() {
        viewModelScope.launch {
            val species = _selectedSpecies.value
            val result = homeRepository.fetchPetList(species)
            Log.i("MYTAG", result.toString())
            if (result is Resource.Success) {
                _petList.value = result.result
            } else {
                // Handle error
            }
        }
    }

    fun setSpeciesFilter(species: String?) {
        _selectedSpecies.value = species
        fetchPets()
    }

    fun fetchPetById(petId: String) {
        viewModelScope.launch {
            try {
                _petByIdFlow.value = Resource.Loading
                val result = homeRepository.fetchPetById(petId)
                Log.i("MYTAG", "FetchPetById: $result")
                _petByIdFlow.value = result
            } catch (e: Exception) {
                _petByIdFlow.value = Resource.Failure(e)
            }
        }
    }

    fun fetchUserById(userId: String?) {
        viewModelScope.launch {
            val result = userId?.let { homeRepository.fetchUserById(it) }
            if (result is Resource.Success) {
                _userById.value = result.result
            }
        }
    }

    fun checkIfLiked(petId: String) {
        viewModelScope.launch {
            // Fetch from repository if pet is already liked
            val isPetLiked = homeRepository.isPetLiked(petId)
            _isLiked.value = isPetLiked
        }
    }

    fun toggleLike(petId: String) {
        viewModelScope.launch {
            _likeStatus.value = Resource.Loading
            val result = homeRepository.toggleLike(petId)
            _likeStatus.value = result
            if (result is Resource.Success) {
                _isLiked.value = !_isLiked.value // Flip the liked status
            }
        }
    }}