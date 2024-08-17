package com.luke.petpal.domain.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.luke.petpal.data.models.Resource
import com.luke.petpal.data.repository.HomeRepository
import com.luke.petpal.data.utils.await
import com.luke.petpal.domain.data.Pet
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val firestore: FirebaseFirestore,
) : HomeRepository {

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun uploadPet(pet: Pet): Resource<Unit> {
        val photoUrls = pet.photos?.mapNotNull { uri ->
            val filename = uri.lastPathSegment ?: return@mapNotNull null
            val storageRef = storage.reference.child("pets/${pet.name}/$filename")
            storageRef.putFile(uri).await()
            storageRef.downloadUrl.await().toString()
        } ?: emptyList()

        val petData = mapOf(
            "userId" to currentUser?.uid,
            "name" to pet.name,
            "species" to pet.species,
            "breed" to pet.breed,
            "gender" to pet.gender,
            "age" to pet.age,
            "weight" to pet.weight,
            "color" to pet.color,
            "photos" to photoUrls
        )

        return try {
            firestore.collection("pets")
                .add(petData)
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

}