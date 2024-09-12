package com.luke.petpal.domain.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.luke.petpal.data.repository.ChatRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : ChatRepository {

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun createOrJoinChat(userAId: String, userBId: String): String {
        val chatId = if (userAId < userBId) "{$userAId}_$userBId" else "{$userBId}_$userAId"

        // Reference to the chat document
        val chatRef = firestore.collection("chats").document(chatId)

        // Check if chat already exists
        val chatSnapshot = chatRef.get().await()
        if (!chatSnapshot.exists()) {
            // If the chat doesn't exist, create it
            val chatData = hashMapOf(
                "chatId" to chatId,
                "participants" to listOf(userAId, userBId),
                "lastMessage" to "",
                "lastMessageTimestamp" to System.currentTimeMillis()
            )
            chatRef.set(chatData).await()
        }

        return chatId
    }

    override suspend fun sendMessage(chatId: String, senderId: String, message: String) {
        val messageData = hashMapOf(
            "senderId" to senderId,
            "message" to message,
            "timestamp" to FieldValue.serverTimestamp()
        )
        firestore.collection("chats").document(chatId).collection("messages").add(messageData)
            .addOnSuccessListener { documentReference ->
                Log.d("MYTAG", "Message sent with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("MYTAG", "Error adding message", e)
            }
    }

    override suspend fun listenForMessages(chatId: String) {
        firestore.collection("chats").document(chatId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("MYTAG", "Listen failed.", e)
                    return@addSnapshotListener
                }
                for (doc in snapshots!!) {
                    Log.d("MYTAG", "${doc.id} => ${doc.data}")
                }
            }
    }

}