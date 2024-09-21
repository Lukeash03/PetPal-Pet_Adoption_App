package com.luke.petpal.data.repository

import android.content.Context
import android.location.Location
import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import com.luke.petpal.data.models.Resource

interface UserProfileRepository {
    val currentUser: FirebaseUser?
    suspend fun uploadProfileImage(uri: Uri): Resource<String>
    suspend fun updateProfileImageUrl(url: String): Resource<Unit>
    suspend fun fetchProfileUrl(): Resource<String>
    suspend fun fetchCurrentLocation(context: Context, onLocationResult: (Location?) -> Unit)
    suspend fun updateLocation(latitude: Double, longitude: Double): Resource<Unit>
}