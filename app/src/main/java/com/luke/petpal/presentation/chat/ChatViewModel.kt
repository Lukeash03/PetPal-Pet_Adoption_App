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

    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels = _channels.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    init {
        getChannels()
    }

    private fun getChannels() {
        firebaseDatabase.getReference("channel").get().addOnSuccessListener {
            val list = mutableListOf<Channel>()
            it.children.forEach { data ->
                val channel = Channel(data.key!!, data.value.toString())
                list.add(channel)
            }
            Log.d("MYTAG", "Channels: $list")
            _channels.value = list
        }
    }

    fun addChannel(name: String) {
        val key = firebaseDatabase.getReference("channel").push().key
        firebaseDatabase.getReference("channel").child(key!!).setValue(name).addOnSuccessListener {
            getChannels()
        }
    }


    fun sendMessage(channelId: String, messageText: String) {
        val message = Message(
            id = firebaseDatabase.reference.push().key ?: UUID.randomUUID().toString(),
            senderId = Firebase.auth.currentUser?.uid ?: "",
            message = messageText,
            createdAt = System.currentTimeMillis(),
            senderName = Firebase.auth.currentUser?.displayName ?: "",
            senderImage = null,
            imageUrl = null
        )
        val key = firebaseDatabase.getReference("messages").child(channelId).push().setValue(message)
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
