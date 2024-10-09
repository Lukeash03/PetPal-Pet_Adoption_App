package com.luke.petpal.presentation

import android.content.Context
import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.auth.FirebaseUser
import com.luke.petpal.data.models.Resource
import com.luke.petpal.data.repository.HomeRepository
import com.luke.petpal.data.repository.UserProfileRepository
import com.luke.petpal.domain.data.PersonalPet
import com.luke.petpal.domain.data.Pet
import com.luke.petpal.domain.data.User
import com.luke.petpal.domain.data.VaccinationEntry
import com.luke.petpal.domain.repository.usecase.CompressImagesUseCase
import com.luke.petpal.domain.repository.usecase.ValidatePetBreed
import com.luke.petpal.domain.repository.usecase.ValidatePetGender
import com.luke.petpal.domain.repository.usecase.ValidatePetName
import com.luke.petpal.domain.repository.usecase.ValidatePetSpecies
import com.luke.petpal.presentation.validation.PetFormEvent
import com.luke.petpal.presentation.validation.PetFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val homeRepository: HomeRepository,
    private val validatePetName: ValidatePetName = ValidatePetName(),
    private val validatePetSpecies: ValidatePetSpecies = ValidatePetSpecies(),
    private val validatePetBreed: ValidatePetBreed = ValidatePetBreed(),
    private val validatePetGender: ValidatePetGender = ValidatePetGender(),
    private val compressImageUseCase: CompressImagesUseCase
) : ViewModel() {

    init {
        fetchPersonalPets()
    }

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

    private val _petUploadStatus = MutableStateFlow<Resource<Unit>?>(null)
    val petUploadStatus: StateFlow<Resource<Unit>?> = _petUploadStatus

    var petValidationState by mutableStateOf(PetFormState())

    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    private val _petList = MutableStateFlow<List<PersonalPet>?>(emptyList())
    val petList: StateFlow<List<PersonalPet>?> = _petList

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

    fun uploadPersonalPet(pet: PersonalPet, vaccinations: List<VaccinationEntry>) {
        viewModelScope.launch {
            _petUploadStatus.value = Resource.Loading
            val result = userProfileRepository.uploadPersonalPet(pet, vaccinations)
            _petUploadStatus.value = result
        }
    }

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

    fun fetchPersonalPets() {
        viewModelScope.launch {
            val result = userProfileRepository.fetchPersonalPets()
            if (result is Resource.Success) {
                _petList.value = result.result
            }
        }
    }
    sealed class ValidationEvent {
        data object Success: ValidationEvent()
    }

}
