package com.luke.petpal.domain.repository.usecase

class ValidatePetBreed {
    fun execute(
        petBreed: String,
    ): ValidationResult {
        return when {
            petBreed.isBlank() -> ValidationResult(
                false,
                "Please enter a breed"
            )
            else ->  ValidationResult(true)
        }
    }
}