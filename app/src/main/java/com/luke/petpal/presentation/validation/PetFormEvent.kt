package com.luke.petpal.presentation.validation

sealed class PetFormEvent {
    data class PetNameChanged(val petName: String) : PetFormEvent()
    data class PetSpeciesChanged(val petSpecies: String) : PetFormEvent()
    data class PetBreedChanged(val petBreed: String) : PetFormEvent()
    data class PetGenderChanged(val petGender: String) : PetFormEvent()

    data object Submit: PetFormEvent()
}