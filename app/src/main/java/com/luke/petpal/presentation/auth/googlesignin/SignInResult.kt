package com.luke.petpal.presentation.auth.googlesignin

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val uid: String,
    val email: String?,
    val username: String?,
    val profileImageUrl: String?
)