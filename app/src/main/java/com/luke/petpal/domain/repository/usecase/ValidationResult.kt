package com.luke.petpal.domain.repository.usecase

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null
)
