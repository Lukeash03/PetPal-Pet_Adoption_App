package com.luke.petpal.domain.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.luke.petpal.data.repository.AuthRepository
import com.luke.petpal.data.models.Resource
import com.luke.petpal.data.utils.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun login(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Resource.Success(result.user!!)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun signUp(
        username: String,
        email: String,
        password: String
    ): Resource<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result?.user?.updateProfile(
                UserProfileChangeRequest.Builder().setDisplayName(username).build()
            )?.await()
            result?.user?.sendEmailVerification()?.await()

            createUserProfile(result.user!!)

            Resource.Success(result.user!!)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    private fun createUserProfile(user: FirebaseUser) {
        val userProfile = mapOf(
            "uid" to user.uid,
            "email" to user.email,
            "profileImageUrl" to "" // or default URL if any
        )
        firestore.collection("users").document(user.uid).set(userProfile)
            .addOnSuccessListener {
                Log.d("Firestore", "User profile created successfully: ${user.uid}")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error creating user profile", e)
            }
    }

    override suspend fun isEmailVerified(): Boolean? {
        return try {
            val user = firebaseAuth.currentUser
            user?.reload()?.await()
            user?.isEmailVerified
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun resendEmailVerification(): Resource<Unit> {
        return try {
            firebaseAuth.currentUser?.sendEmailVerification()?.await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Resource<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

}