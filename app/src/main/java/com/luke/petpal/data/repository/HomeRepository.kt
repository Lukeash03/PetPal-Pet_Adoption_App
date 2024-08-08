package com.luke.petpal.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import com.luke.petpal.data.models.Resource

interface HomeRepository {
    val currentUser: FirebaseUser?
    suspend fun uploadProfileImage(uri: Uri): Resource<String>
    suspend fun updateProfileImageUrl(url: String): Resource<Unit>
    suspend fun fetchProfileUrl(): Resource<String>
    fun logout()
}