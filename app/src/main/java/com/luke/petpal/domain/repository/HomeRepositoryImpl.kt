package com.luke.petpal.domain.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.luke.petpal.data.models.Resource
import com.luke.petpal.data.repository.HomeRepository
import com.luke.petpal.domain.data.Pet
import com.luke.petpal.domain.data.User
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val firestore: FirebaseFirestore,
) : HomeRepository {

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun uploadPet(pet: Pet): Resource<Unit> {
        val uniquePetId = UUID.randomUUID().toString()

        val photoUri = pet.photos?.map { Uri.parse(it) }
        val photoUrls = photoUri?.mapNotNull { uri ->
            val filename = uri.lastPathSegment ?: return@mapNotNull null
            val storageRef = storage.reference.child("pets/$uniquePetId/$filename")
            storageRef.putFile(uri).await()
            storageRef.downloadUrl.await().toString()
        } ?: emptyList()

        val dob = pet.dob?.toString()?.toLongOrNull()
        val weight = pet.weight?.toString()?.toIntOrNull()

        val petData = mapOf(
            "petId" to uniquePetId,
            "userId" to currentUser?.uid,
            "name" to pet.name,
            "species" to pet.species,
            "breed" to pet.breed,
            "gender" to pet.gender,
            "dob" to dob,
            "weight" to weight,
            "color" to pet.color,
            "description" to pet.description,
            "vaccineStatus" to pet.vaccinationStatus,
            "publishDate" to pet.publishDate,
            "photos" to photoUrls,
        )

        return try {
            val documentRef = firestore.collection("pets")
                .add(petData)
                .await()
            documentRef.update("id", documentRef.id)
            Resource.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun fetchPetList(): Resource<List<Pet>> {
        return try {
            val petList = firestore.collection("pets")
                .get()
                .await()
                .documents
                .mapNotNull { document ->
                    val pet = document.toObject(Pet::class.java)
                    pet?.copy(
                        photos = pet.photos?.map { it } ?: emptyList(),
                        documentId = document.id
                    )
//                    pet
                }

            Log.i("MYTAG", "fetchPetList petList: $petList ")
            Resource.Success(petList)
        } catch (e: Exception) {
            Log.i("MYTAG", "fetchPetList: $e")
            Resource.Failure(e)
        }
    }

    override suspend fun fetchPetById(petId: String): Resource<Pet> {
        return try {
            val documentSnapshot: DocumentSnapshot =
                firestore.collection("pets").document(petId).get().await()
            val pet = documentSnapshot.toObject(Pet::class.java)

            Resource.Success(pet!!)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun fetchUserById(userId: String): Resource<User> {
        return try {
            val documentSnapshot: DocumentSnapshot =
                firestore.collection("users").document(userId).get().await()
            val user = documentSnapshot.toObject(User::class.java)

            Resource.Success(user!!)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

}