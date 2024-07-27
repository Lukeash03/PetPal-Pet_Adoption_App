package com.luke.petpal.data.repository

import com.google.firebase.auth.FirebaseUser
import com.luke.petpal.data.models.Resource

interface AuthRepository {
    val currentUser: FirebaseUser?
    suspend fun login(email: String, password: String): Resource<FirebaseUser>
    suspend fun signUp(username: String, email: String, password: String): Resource<FirebaseUser>
//    suspend fun loginWithGoogle(idToken: String): Resource<FirebaseUser>
    suspend fun isEmailVerified(): Boolean?
    suspend fun resendEmailVerification(): Resource<Unit>
    suspend fun sendPasswordResetEmail(email: String): Resource<Unit>
    fun logout()
}