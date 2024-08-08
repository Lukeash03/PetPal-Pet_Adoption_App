package com.luke.petpal.domain.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.luke.petpal.data.models.Resource
import com.luke.petpal.data.repository.HomeRepository
import com.luke.petpal.data.utils.await
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val firestore: FirebaseFirestore,
) : HomeRepository {

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun uploadProfileImage(uri: Uri): Resource<String> {
        return try {
            val storageRef =
                storage.reference.child("profileImages/${firebaseAuth.currentUser?.uid}")
            val uploadTask = storageRef.putFile(uri).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await()
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
            user?.updateProfile(userProfileChangeRequest)?.await()

            Log.i("MyTag", "UpdateProfileUrl: UserId: $user Started")

            user?.uid?.let { uid ->
                firestore.collection("users").document(uid).update("profileImageUrl", url).await()
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
        return try {
            val user = firebaseAuth.currentUser
            user?.uid?.let { uid ->
                val documentSnapshot = firestore.collection("users").document(user.uid).get().await()
                val profileImageUrl = documentSnapshot.getString("profileImageUrl")
                if (profileImageUrl != null) {
                    Resource.Success(profileImageUrl)
                } else {
                    Resource.Failure(Exception("Profile image not found"))
                }
            } ?: Resource.Failure(Exception("User is not logged in"))
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

}