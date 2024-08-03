package com.luke.petpal.domain.repository.usecase

class ValidatePassword {

    fun execute(password: String): ValidationResult {
        if (password.length < 6) {
            return ValidationResult(
                false,
                "Password must be of at least 6 characters long"
            )
        }
        if (!password.any() { it.isUpperCase() }) {
            return ValidationResult(
                false,
                "Password must contain at least one uppercase letter"
            )
        }
        if (!password.any() { it.isLowerCase() }) {
            return ValidationResult(
                false,
                "Password must contain at least one lowercase letter"
            )
        }
        if (!password.any() { it.isDigit() }) {
            return ValidationResult(
                false,
                "Password must contain at least one digit"
            )
        }
        return ValidationResult(
            true
        )
    }
}