package com.luke.petpal.domain.repository.usecase

class ValidatePetGender {
    fun execute(
        petGender: String
    ): ValidationResult {
        return when {
            petGender.isBlank() -> ValidationResult(
                false,
                "Please select a gender"
            )
            else ->  ValidationResult(true)
        }
    }
}