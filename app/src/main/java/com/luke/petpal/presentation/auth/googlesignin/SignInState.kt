package com.luke.petpal.presentation.auth.googlesignin

import android.content.IntentSender

data class SignInState(
    val signInIntent: IntentSender? = null,
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)
