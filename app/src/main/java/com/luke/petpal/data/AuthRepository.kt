package com.luke.petpal.data

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    val currentUser: FirebaseUser?
    suspend fun login(email: String, password: String): Resource<FirebaseUser>
    suspend fun signUp(username: String, email: String, password: String): Resource<FirebaseUser>
    suspend fun isEmailVerified(): Boolean?
    suspend fun resendEmailVerification(): Resource<Unit>
    suspend fun sendPasswordResetEmail(email: String): Resource<Unit>
    fun logout()
}