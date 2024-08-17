package com.luke.petpal.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import com.luke.petpal.data.models.Resource

interface AuthRepository {
    val currentUser: FirebaseUser?
    suspend fun login(email: String, password: String): Resource<FirebaseUser>
    suspend fun signUp(username: String, email: String, password: String): Resource<FirebaseUser>
    suspend fun isEmailVerified(): Boolean?
    suspend fun resendEmailVerification(): Resource<Unit>
    suspend fun sendPasswordResetEmail(email: String): Resource<Unit>
//    suspend fun uploadProfileImage(uri: Uri): Resource<String>
//    suspend fun updateProfileImageUrl(url: String): Resource<Unit>
//    suspend fun fetchProfileUrl(): Resource<String>
//    fun logout()
}