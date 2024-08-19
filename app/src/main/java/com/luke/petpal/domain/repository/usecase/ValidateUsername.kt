package com.luke.petpal.domain.repository.usecase

import android.util.Patterns

class ValidateUsername {

    fun execute(username: String): ValidationResult {
        if (username.isBlank()) {
            return ValidationResult(
                false,
                "Username cannot be empty"
            )
        }

        if (username.length < 3) {
            return ValidationResult(
                false,
                "Username must be at least 3 characters long"
            )
        }

        if (!username.all { it.isLetterOrDigit() || it == '_' }) {
            return ValidationResult(
                false,
                "Username can only contain letters, digits, and underscores"
            )
        }
        return ValidationResult(
            true
        )
    }

}