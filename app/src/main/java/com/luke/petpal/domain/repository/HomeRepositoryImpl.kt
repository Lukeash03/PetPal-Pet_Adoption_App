package com.luke.petpal.domain.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.luke.petpal.data.models.Resource
import com.luke.petpal.data.repository.HomeRepository
import com.luke.petpal.data.utils.await
import com.luke.petpal.domain.data.Pet
import java.io.File
import java.io.FileOutputStream
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
            val storageRef = storage.reference.child("pets/${pet.name}/$filename")
            storageRef.putFile(uri).await()
            storageRef.downloadUrl.await().toString()
        } ?: emptyList()

        val age = pet.age?.toString()?.toIntOrNull()
        val weight = pet.weight?.toString()?.toIntOrNull()

//        val age = pet.age?.takeIf { it.isNotEmpty() }?.toIntOrNull() ?: 0 // Default to 0 if empty
//        val weight = pet.weight?.takeIf { it.isNotEmpty() }?.toIntOrNull() ?: 0 // Default to 0 if empty
//        val color = pet.color.takeIf { it?.isNotEmpty() ?:  } ?: "Unknown" // Default to "Unknown" if empty


        val petData = mapOf(
            "userId" to currentUser?.uid,
            "name" to pet.name,
            "species" to pet.species,
            "breed" to pet.breed,
            "gender" to pet.gender,
            "age" to age,
            "weight" to weight,
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

    override suspend fun fetchPetList(): Resource<List<Pet>> {
        return try {
            Log.i("MYTAG", "HomeRepo.fetchPetList starting")
            val petList = firestore.collection("pets")
                .get()
                .await()
                .documents
                .mapNotNull { document ->
                    val pet = document.toObject(Pet::class.java)
                    pet?.copy(
                        photos = pet.photos?.map { it.toString() } ?: emptyList()
                    )
                }

            Log.i("MYTAG", "fetchPetList petList: $petList ")

            Resource.Success(petList)
        } catch (e: Exception) {
            Log.i("MYTAG", "fetchPetList: $e")
            Resource.Failure(e)
        }
    }

    override suspend fun fetchPetById(petId: String): Resource<Pet?> {
        return try {
            val documentSnapshot: DocumentSnapshot =
                firestore.collection("pets").document(petId).get().await()
            val pet = documentSnapshot.toObject(Pet::class.java)

            Resource.Success(pet)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

}