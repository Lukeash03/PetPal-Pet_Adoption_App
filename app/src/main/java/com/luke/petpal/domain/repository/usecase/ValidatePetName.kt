package com.luke.petpal.domain.repository.usecase

class ValidatePetName {
    fun execute(
        petName: String,
    ): ValidationResult {
        return when {
            petName.isBlank() -> ValidationResult(
                false,
                "Please enter a pet name."
            )
            else ->  ValidationResult(true)
        }
    }
}