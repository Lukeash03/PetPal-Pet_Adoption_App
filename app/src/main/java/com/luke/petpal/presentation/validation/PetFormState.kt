package com.luke.petpal.presentation.validation

data class PetFormState(
    val petName: String = "",
    val petNameError: String? = null,
    val petSpecies: String = "",
    val petSpeciesError: String? = null,
    val petBreed: String = "",
    val petBreedError: String? = null,
    val petGender: String = "",
    val petGenderError: String? = null,
)