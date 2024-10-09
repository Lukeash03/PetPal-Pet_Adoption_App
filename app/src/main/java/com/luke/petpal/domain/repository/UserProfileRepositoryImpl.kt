package com.luke.petpal.domain.repository

import android.content.Context
import android.location.Location
import android.net.Uri
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.luke.petpal.data.models.Resource
import com.luke.petpal.data.repository.UserProfileRepository
import com.luke.petpal.data.utils.awaitC
import com.luke.petpal.domain.data.PersonalPet
import com.luke.petpal.domain.data.Pet
import com.luke.petpal.domain.data.VaccinationEntry
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class UserProfileRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val firestore: FirebaseFirestore,
) : UserProfileRepository {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun uploadProfileImage(uri: Uri): Resource<String> {
        return try {
            val storageRef =
                storage.reference.child("profileImages/${firebaseAuth.currentUser?.uid}")
            val uploadTask = storageRef.putFile(uri).awaitC()
            val downloadUrl = uploadTask.storage.downloadUrl.awaitC()
            Resource.Success(downloadUrl.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun updateProfileImageUrl(url: String): Resource<Unit> {
        return try {
            val user = firebaseAuth.currentUser
            Log.i("MyTag", "UpdateProfileUrl: UserId: $user")
            val userProfileChangeRequest = UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(url))
                .build()
            user?.updateProfile(userProfileChangeRequest)?.awaitC()

            Log.i("MyTag", "UpdateProfileUrl: UserId: $user Started")

            user?.uid?.let { uid ->
                firestore.collection("users").document(uid).update("profileImageUrl", url).awaitC()
                Log.i("MyTag", "UpdateProfileUrl: Firestore updated with new profileImageUrl")
            }

            Log.i("MyTag", "UpdateProfileUrl: UserId: $user Success")
            Resource.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun fetchProfileUrl(): Resource<String> {
        val user =
            firebaseAuth.currentUser ?: return Resource.Failure(Exception("User is not logged in"))

        return try {
            val documentSnapshot = firestore.collection("users").document(user.uid).get().awaitC()
            Log.i("MyTag", "fetchProfileUrl: ${documentSnapshot.exists()}")
            val profileImageUrl = documentSnapshot.getString("profileImageUrl")
            if (profileImageUrl != null) {
                Log.i("MyTag", "fetchProfileUrl: Url: $profileImageUrl")
                Resource.Success(profileImageUrl)
            } else {
                Log.i("MyTag", "fetchProfileUrl: No image")
                Resource.Failure(Exception("Profile image not found"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun fetchCurrentLocation(
        context: Context,
        onLocationResult: (Location?) -> Unit
    ) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    onLocationResult(location)
                }.addOnFailureListener { exception ->
                    Log.e("LocationRepo", "Failed to get location", exception)
                    onLocationResult(null) // Handle failure
                }
        } catch (e: SecurityException) {
            // Handle permission denied exception
        }
    }

    override suspend fun updateLocation(latitude: Double, longitude: Double): Resource<Unit> {
        return try {
            val user = firebaseAuth.currentUser
            user?.uid?.let { uid ->
                val locationMap = mapOf(
                    "location" to mapOf("latitude" to latitude, "longitude" to longitude)
                )
                firestore.collection("users").document(uid).update(locationMap).awaitC()
                Resource.Success(Unit)
            } ?: Resource.Failure(Exception("User not logged in"))
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun uploadPersonalPet(
        pet: PersonalPet,
        vaccinations: List<VaccinationEntry>
    ): Resource<Unit> {
        val photoUri = pet.photos?.map { Uri.parse(it) }
        val photoUrls = photoUri?.mapNotNull { uri ->
            val filename = uri.lastPathSegment ?: return@mapNotNull null
            val storageRef = storage.reference.child("pets/${UUID.randomUUID()}/$filename")
            storageRef.putFile(uri).await()
            storageRef.downloadUrl.await().toString()
        } ?: emptyList()

        val dob = pet.dob?.toString()?.toLongOrNull()

        val petData = mapOf(
            "userId" to currentUser?.uid,
            "name" to pet.name,
            "species" to pet.species,
            "breed" to pet.breed,
            "gender" to pet.gender,
            "dob" to dob,
            "publishDate" to pet.publishDate,
            "photos" to photoUrls,
        )

        return try {
            val documentRef = firestore.collection("personalPets")
                .add(petData)
                .await()
            documentRef.update("petId", documentRef.id)

            val vaccinationRef = documentRef.collection("vaccinations")
            vaccinations.forEach { vaccination ->
                val vaccinationData = mapOf(
                    "petId" to documentRef.id,
                    "vaccineName" to vaccination.vaccineName,
                    "doctorName" to vaccination.doctorName,
                    "date" to vaccination.date,
                    "reminder" to vaccination.reminder,
                    "reminderDate" to vaccination.reminderDate
                )
                vaccinationRef.add(vaccinationData).await()
            }

            Resource.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }

    }

    override suspend fun fetchPersonalPets(): Resource<List<PersonalPet>> {
        val currentUserId = currentUser?.uid

        return try {
            val petList = firestore.collection("personalPets")
                .get()
                .await()
                .documents
                .mapNotNull { document ->
                    val pet = document.toObject(PersonalPet::class.java)
                    if (pet?.userId == currentUserId) {
                        pet?.copy(
                            photos = pet.photos?.map { it } ?: emptyList(),
                        )
                    } else {
                        null
                    }
                }
            Resource.Success(petList)
        } catch (e: Exception) {
            Resource.Failure(e)
        }

    }

}