package com.luke.petpal.presentation

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.luke.petpal.UserPreferencesRepository
import com.luke.petpal.data.models.Resource
import com.luke.petpal.data.repository.ChatRepository
import com.luke.petpal.data.repository.HomeRepository
import com.luke.petpal.data.repository.UserProfileRepository
import com.luke.petpal.domain.data.Pet
import com.luke.petpal.domain.data.User
import com.luke.petpal.domain.repository.usecase.CompressImagesUseCase
import com.luke.petpal.domain.repository.usecase.ValidatePetBreed
import com.luke.petpal.domain.repository.usecase.ValidatePetGender
import com.luke.petpal.domain.repository.usecase.ValidatePetName
import com.luke.petpal.domain.repository.usecase.ValidatePetSpecies
import com.luke.petpal.presentation.auth.googlesignin.GoogleAuthUIClient
import com.luke.petpal.presentation.validation.PetFormEvent
import com.luke.petpal.presentation.validation.PetFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val userProfileRepository: UserProfileRepository,
    private val chatRepository: ChatRepository,
    private val googleAuthUIClient: GoogleAuthUIClient,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val validatePetName: ValidatePetName = ValidatePetName(),
    private val validatePetSpecies: ValidatePetSpecies = ValidatePetSpecies(),
    private val validatePetBreed: ValidatePetBreed = ValidatePetBreed(),
    private val validatePetGender: ValidatePetGender = ValidatePetGender(),
    private val compressImageUseCase: CompressImagesUseCase
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

    private val _updateStatus = MutableStateFlow<Resource<Unit>?>(null)
    val updateStatus: StateFlow<Resource<Unit>?> = _updateStatus

    private val _petList = MutableStateFlow<List<Pet>?>(emptyList())
    val petList: StateFlow<List<Pet>?> = _petList

    private val _userPetList = MutableStateFlow<List<Pet>?>(emptyList())
    val userPetList: StateFlow<List<Pet>?> = _userPetList

    private val _likedPetList = MutableStateFlow<List<Pet>?>(emptyList())
    val likedPetList: StateFlow<List<Pet>?> = _likedPetList

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

    private val _createChatState = MutableStateFlow<Resource<String>?>(null)
    val createChatState: StateFlow<Resource<String>?> = _createChatState

    val isDarkModeActive = userPreferencesRepository.userPreferencesFlow.map { it.isDarkModeActive }

    var petValidationState by mutableStateOf(PetFormState())

    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()


    init {
        fetchPets()
    }

    val currentUser: FirebaseUser? get() = homeRepository.currentUser

    fun onEvent(event: PetFormEvent) {
        when (event) {
            is PetFormEvent.PetBreedChanged -> {
                petValidationState = petValidationState.copy(petBreed = event.petBreed)
            }
            is PetFormEvent.PetGenderChanged -> {
                petValidationState = petValidationState.copy(petGender = event.petGender)
            }
            is PetFormEvent.PetNameChanged -> {
                petValidationState = petValidationState.copy(petName = event.petName)
            }
            is PetFormEvent.PetSpeciesChanged -> {
                petValidationState = petValidationState.copy(petSpecies = event.petSpecies)
            }
            is PetFormEvent.Submit -> {
                submitData()
            }
        }
    }

    private fun submitData() {
        val petNameResult = validatePetName.execute(petValidationState.petName)
        val petSpeciesResult = validatePetSpecies.execute(petValidationState.petSpecies)
        val petBreedResult = validatePetBreed.execute(petValidationState.petBreed)
        val petGenderResult = validatePetGender.execute(petValidationState.petGender)

        val hasError = listOf(
            petNameResult,
            petSpeciesResult,
            petBreedResult,
            petGenderResult
        ).any { !it.successful }

        if (hasError) {
            petValidationState = petValidationState.copy(
                petNameError = petNameResult.errorMessage,
                petSpeciesError = petSpeciesResult.errorMessage,
                petBreedError = petBreedResult.errorMessage,
                petGenderError = petGenderResult.errorMessage
            )
            return
        }

        viewModelScope.launch {
            validationEventChannel.send(ValidationEvent.Success)
        }
    }

    fun compressImages(imageStrings: List<String>, context: Context): List<String> {
        return compressImageUseCase.execute(imageStrings, context)
    }

    fun updateIsDarkModeActive(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateIsDarkModeActive(isDarkModeActive)
        }
    }

    fun logout() {
        viewModelScope.launch {
            homeRepository.logout()
            googleAuthUIClient.signOut()
            _loginFlow.value = null
        }
    }

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

    fun uploadPet(pet: Pet) {
        viewModelScope.launch {
            val result = homeRepository.uploadPet(pet)
            _uploadStatus.value = result
        }
    }

    fun updatePet(pet: Pet) {
        viewModelScope.launch {
            val result = homeRepository.updatePet(pet)
            _updateStatus.value = result
        }
    }

    fun fetchPets() {
        viewModelScope.launch {
            val species = _selectedSpecies.value
            val result = homeRepository.fetchPetList(species)
            Log.i("HomeViewModel", "fetchPets: $result")
            if (result is Resource.Success) {
                _petList.value = result.result
            } else {
                // Handle error
            }
        }
    }

    fun fetchUserPets() {
        viewModelScope.launch {
            val species = _selectedSpecies.value
            val result = homeRepository.fetchUserPetList(species)
            Log.i("MYTAG", result.toString())
            if (result is Resource.Success) {
                _userPetList.value = result.result
            } else {
                // Handle error
            }
        }
    }

    fun fetchLikedPets() {
        viewModelScope.launch {
            val result = homeRepository.fetchLikedPetList()
            if (result is Resource.Success) {
                _likedPetList.value = result.result
            } else {
                // error
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
                Log.i("HomeViewModel", "FetchPetById: $result")
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
    }

    fun createChat(petOwnerId: String) {
        viewModelScope.launch {
            _createChatState.value = Resource.Loading
            val result = chatRepository.createChat(petOwnerId)
            _createChatState.value = result
        }
    }

    sealed class ValidationEvent {
        data object Success: ValidationEvent()
    }
}