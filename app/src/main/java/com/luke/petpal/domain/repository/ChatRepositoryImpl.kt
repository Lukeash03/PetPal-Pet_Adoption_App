package com.luke.petpal.domain.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.luke.petpal.data.models.Resource
import com.luke.petpal.data.repository.ChatRepository
import com.luke.petpal.domain.data.Chat
import com.luke.petpal.domain.data.Message
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : ChatRepository {

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun createChat(petOwnerId: String): Resource<String> {
        val user = currentUser?.uid ?: return Resource.Failure(Exception("User not authenticated"))
        val chatId = if (user < petOwnerId) "${user}_$petOwnerId" else "${petOwnerId}_$user"

        val chatRef = firestore.collection("chats").document(chatId)
        val chatSnapshot = chatRef.get().await()

        return if (!chatSnapshot.exists()) {
            // If the chat doesn't exist, create it
            val chatData = hashMapOf(
                "chatId" to chatId,
                "participants" to listOf(user, petOwnerId),
                "lastMessage" to "",
                "lastMessageTimestamp" to System.currentTimeMillis()
            )
            chatRef.set(chatData).await()
            Resource.Success(chatId)
        } else {
            Resource.Success(chatId)
        }
    }

    override suspend fun sendMessage(chatId: String, message: String) {
        val senderId = currentUser?.uid.toString()
        val messageData = hashMapOf(
            "senderId" to senderId,
            "message" to message,
            "timestamp" to FieldValue.serverTimestamp()
        )

        val chatRef = firestore.collection("chats").document(chatId)
        val messageRef = chatRef.collection("messages")

        try {
            messageRef.add(messageData).await()

            chatRef.update(
                mapOf(
                    "lastMessage" to message,
                    "lastMessageTimestamp" to System.currentTimeMillis()
                )
            ).await()
            Log.d("ChatRepository", "Message sent successfully")
        } catch (e: Exception) {
            Log.w("ChatRepository", "Error adding message", e)
        }
    }

    override suspend fun fetchMessages(chatId: String): Flow<Resource<List<Message>>> =
        callbackFlow {
            try {
                trySend(Resource.Loading)
                val messageRef = firestore.collection("chats")
                    .document(chatId)
                    .collection("messages")
                    .orderBy("timestamp", Query.Direction.ASCENDING)

                val listener = messageRef.addSnapshotListener { snapshot, e ->
                    if (e != null || snapshot == null) {
                        trySend(Resource.Failure(Exception("Error fetching messages")))
                        Log.w("MYTAG", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    val messageList = snapshot.documents.map { document ->
                        document.toObject(Message::class.java)!!
                    }
                    Log.i("ChatRepositoryImpl", "ChatId: $chatId")
                    Log.i("ChatRepositoryImpl", "MessageList: $messageList")
                    trySend(Resource.Success(messageList))
                }

                awaitClose { listener.remove() }
            } catch (e: Exception) {
                trySend(Resource.Failure(e))
            }
        }

    override suspend fun fetchUserChats(): Resource<List<Chat>> {
        val userId = currentUser?.uid ?: return Resource.Failure(Exception("User not authenticated"))

        return try {
            val chatsQuery = firestore.collection("chats")
                .whereArrayContains("participants", userId)
                .get()
                .await()

            val chatList = chatsQuery.documents.mapNotNull { document ->
                val chat = document.toObject(Chat::class.java)

                val otherUserId = chat?.participants?.firstOrNull { it != userId } ?: return@mapNotNull null

                val otherUserDoc = firestore.collection("users").document(otherUserId).get().await()
                val otherUserName = otherUserDoc.getString("username") ?: ""
                val otherUserProfileImage = otherUserDoc.getString("profileImageUrl") ?: ""

                chat.copy(
                    senderName = otherUserName,
                    senderProfileImageUrl = otherUserProfileImage
                )
            }

            Resource.Success(chatList)
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

}