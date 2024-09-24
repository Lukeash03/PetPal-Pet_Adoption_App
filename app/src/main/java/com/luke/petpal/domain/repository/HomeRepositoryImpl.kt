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
        val photoUri = pet.photos?.map { Uri.parse(it) }
        val photoUrls = photoUri?.mapNotNull { uri ->
            val filename = uri.lastPathSegment ?: return@mapNotNull null
            val storageRef = storage.reference.child("pets/${UUID.randomUUID()}/$filename")
            storageRef.putFile(uri).await()
            storageRef.downloadUrl.await().toString()
        } ?: emptyList()

        val dob = pet.dob?.toString()?.toLongOrNull()
        val weight = pet.weight?.toString()?.toIntOrNull()

        val petData = mapOf(
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
            documentRef.update("petId", documentRef.id)
            Resource.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun updatePet(pet: Pet): Resource<Unit> {
        val newPhotoUris = pet.photos?.filter { it.startsWith("content://") }?.map { Uri.parse(it) }

        val newPhotoUrls = newPhotoUris?.mapNotNull { uri ->
            val filename = uri.lastPathSegment ?: return@mapNotNull null
            val storageRef = storage.reference.child("pets/${pet.petId}/$filename") // Use existing petId
            storageRef.putFile(uri).await()
            storageRef.downloadUrl.await().toString()
        } ?: emptyList()

        val existingPhotoUrls = pet.photos?.filter { it.startsWith("https://") } ?: emptyList()
        val finalPhotosUrls = existingPhotoUrls + newPhotoUrls

        val dob = pet.dob?.toString()?.toLongOrNull()
        val weight = pet.weight?.toString()?.toIntOrNull()

        val updatedPetData = mapOf(
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
            "photos" to finalPhotosUrls
        )

        Log.i("HomeRepositoryImpl", "Pet: $pet")
        return try {
            firestore.collection("pets").document(pet.petId.toString())
                .update(updatedPetData)
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun fetchPetList(species: String?): Resource<List<Pet>> {
        return try {
            val query = if (species != null) {
                firestore.collection("pets").whereEqualTo("species", species)
            } else {
                firestore.collection("pets")
            }

            val petList = query.get()
                .await()
                .documents
                .mapNotNull { document ->
                    val pet = document.toObject(Pet::class.java)
                    pet?.copy(
                        photos = pet.photos?.map { it } ?: emptyList(),
//                        petId = document.id
                    )
                }

            Log.i("HomeRepositoryImpl", "fetchPetList: $petList ")
            Resource.Success(petList)
        } catch (e: Exception) {
            Log.i("HomeRepositoryImpl", "fetchPetList: $e")
            Resource.Failure(e)
        }
    }

    override suspend fun fetchUserPetList(species: String?): Resource<List<Pet>> {
        return try {
            Log.i("HomeRepositoryImpl", "fetchUserPetList-> ${currentUser?.uid.toString()}")
            val query = if (species != null) {
                firestore.collection("pets")
                    .whereEqualTo("userId", currentUser?.uid.toString())
                    .whereEqualTo("species", species)
            } else {
                firestore.collection("pets")
                    .whereEqualTo("userId", currentUser?.uid.toString())
            }

            val petList = query.get()
                .await()
                .documents
                .mapNotNull { document ->
                    val pet = document.toObject(Pet::class.java)
                    pet?.copy(
                        photos = pet.photos?.map { it } ?: emptyList(),
                        petId = document.id
                    )
                }

            Log.i("HomeRepositoryImpl", "fetchUserPetList: $petList ")
            Resource.Success(petList)
        } catch (e: Exception) {
            Log.i("HomeRepositoryImpl", "fetchUserPetList: $e")
            Resource.Failure(e)
        }
    }

    override suspend fun fetchLikedPetList(): Resource<List<Pet>> {
        return try {
            val userId = currentUser?.uid.toString()
            val likedPetIds = firestore.collection("users")
                .document(userId)
                .collection("likedPets")
                .get()
                .await()
                .documents
                .mapNotNull { it.getString("petId") }

            val petList = likedPetIds.mapNotNull { petId ->
                val petDocument = firestore.collection("pets")
                    .document(petId)
                    .get()
                    .await()
                petDocument.toObject(Pet::class.java)?.copy(petId = petId)
            }
            Log.i("HomeRepositoryImpl", "fetchLikedPetList petList: $petList ")
            Resource.Success(petList)
        } catch (e: Exception) {
            Log.i("HomeRepositoryImpl", "fetchLikedPets: $e")
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

            Log.i("HomeRepositoryImpl", "Before success: $user")
            Resource.Success(user!!)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun isPetLiked(petId: String): Boolean {
        return try {
            // Get reference to the "likedPets" collection for the current user
            val likesRef = firestore.collection("users")
                .document(currentUser?.uid!!)
                .collection("likedPets")

            // Query Firestore to check if the petId exists in the "likedPets" collection
            val snapshot = likesRef.document(petId).get().await()

            // If the document exists, it means the pet is liked
            snapshot.exists()
        } catch (e: Exception) {
            Log.e("MYTAG", "Error checking if pet is liked: ${e.message}")
            false
        }
    }

    override suspend fun toggleLike(petId: String): Resource<Unit> {
        val likesRef =
            firestore.collection("users").document(currentUser?.uid!!).collection("likedPets")

        return try {
            val likedPetDoc = likesRef.document(petId).get().await()

            if (likedPetDoc.exists()) {
                // Pet is already liked, so remove it from the likedPets collection
                likesRef.document(petId).delete().await()
                Resource.Success(Unit) // Return success after deletion
            } else {
                // Pet is not liked, so add it to the likedPets collection
                val likeData = hashMapOf("petId" to petId)
                likesRef.document(petId).set(likeData).await()
                Resource.Success(Unit) // Return success after adding
            }
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

}