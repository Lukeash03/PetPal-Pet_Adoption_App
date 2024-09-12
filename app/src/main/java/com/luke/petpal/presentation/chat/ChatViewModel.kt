package com.luke.petpal.presentation.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.luke.petpal.domain.data.Channel
import com.luke.petpal.domain.data.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    fun sendMessage(receiverId: String, messageText: String) {
        val senderId = Firebase.auth.currentUser?.uid ?: return
        val chatId = generateChatId(senderId, receiverId)
        val message = Message(
            id = firebaseDatabase.reference.push().key ?: UUID.randomUUID().toString(),
            senderId = senderId,
            message = messageText,
            createdAt = System.currentTimeMillis(),
            senderName = Firebase.auth.currentUser?.displayName ?: "",
            senderImage = null,
            imageUrl = null
        )
        firebaseDatabase.getReference("chats").child(chatId).child("messages").push().setValue(message)
    }

    private fun generateChatId(user1: String, user2: String): String {
        return if (user1 < user2) {
            "${user1}_$user2"
        } else {
            "${user2}_$user1"
        }
    }


    fun listenForMessages(channelId: String) {
        firebaseDatabase.getReference("messages").child(channelId).orderByChild("createdAt")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Message>()
                    snapshot.children.forEach { data ->
                        val message = data.getValue(Message::class.java)
                        message?.let {
                            list.add(it)
                        }
                    }
                    _messages.value = list
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}
