package com.luke.petpal.domain.repository.usecase

import org.junit.Assert.*

import org.junit.Before

class ValidatePasswordTest {

    private lateinit var validatePassword: ValidatePassword

    @Before
    fun setUp() {
        validatePassword = ValidatePassword()
    }
}