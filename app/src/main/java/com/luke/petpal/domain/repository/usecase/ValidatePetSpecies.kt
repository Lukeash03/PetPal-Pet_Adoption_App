package com.luke.petpal.domain.repository.usecase

class ValidatePetSpecies {
    fun execute(
        petSpecies: String,
    ): ValidationResult {
        return when {
            petSpecies.isBlank() -> ValidationResult(
                false,
                "Please select a species"
            )
            else ->  ValidationResult(true)
        }
    }
}