package com.luke.petpal.domain.repository.usecase

import android.util.Patterns

class ValidateEmail {

    fun execute(email: String): ValidationResult {
        if (email.isBlank()) {
            return ValidationResult(
                false,
                "Please enter an email address"
            )
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationResult(
                false,
                "Enter a valid email address"
            )
        }
        return ValidationResult(
            true
        )
    }

}