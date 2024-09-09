package com.luke.petpal.data.repository

import com.google.firebase.auth.FirebaseUser

interface ChatRepository {
    val currentUser: FirebaseUser?
    suspend fun createOrJoinChat(participants: List<String>)
    suspend fun sendMessage(chatId: String, senderId: String, message: String)
    suspend fun listenForMessages(chatId: String)
}