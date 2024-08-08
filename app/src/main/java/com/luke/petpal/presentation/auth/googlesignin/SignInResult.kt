package com.luke.petpal.presentation.auth.googlesignin

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String,
    val username: String?,
    val profilePicUrl: String?
)