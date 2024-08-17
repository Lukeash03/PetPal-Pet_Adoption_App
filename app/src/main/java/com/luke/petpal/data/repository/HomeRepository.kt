package com.luke.petpal.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import com.luke.petpal.data.models.Resource
import com.luke.petpal.domain.data.Pet

interface HomeRepository {
    val currentUser: FirebaseUser?
    suspend fun uploadPet(pet: Pet): Resource<Unit>
    fun logout()
}