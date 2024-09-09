package com.luke.petpal.domain.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.luke.petpal.data.repository.ChatRepository
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : ChatRepository {

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun createOrJoinChat(participants: List<String>) {
//        val chatQuery = firestore

        val chatData = hashMapOf(
            "participants" to participants,
            "lastMessage" to "",
            "timestamp" to FieldValue.serverTimestamp()
        )
        firestore.collection("chats").add(chatData)
            .addOnSuccessListener { documentReference ->
                Log.d("MYTAG", "Chat created with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("MYTAG", "Error adding document", e)
            }
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