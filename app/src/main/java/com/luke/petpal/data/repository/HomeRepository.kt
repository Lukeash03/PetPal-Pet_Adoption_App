package com.luke.petpal.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import com.luke.petpal.data.models.Resource
import com.luke.petpal.domain.data.Pet
import com.luke.petpal.domain.data.User

interface HomeRepository {
    val currentUser: FirebaseUser?
    suspend fun uploadPet(pet: Pet): Resource<Unit>
    suspend fun fetchPetList(species: String? = null): Resource<List<Pet>>
    suspend fun fetchPetById(petId: String): Resource<Pet>
    suspend fun fetchUserById(userId: String): Resource<User>
    suspend fun isPetLiked(petId: String): Boolean
    suspend fun toggleLike(petId: String): Resource<Unit>
    fun logout()
}