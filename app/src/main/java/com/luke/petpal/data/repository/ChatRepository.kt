package com.luke.petpal.data.repository

import com.google.firebase.auth.FirebaseUser
import com.luke.petpal.data.models.Resource
import com.luke.petpal.domain.data.Chat
import com.luke.petpal.domain.data.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    val currentUser: FirebaseUser?
    suspend fun createChat(petOwnerId: String): Resource<String>
    suspend fun sendMessage(chatId: String, message: String)
    suspend fun fetchMessages(chatId: String): Flow<Resource<List<Message>>>
    suspend fun fetchUserChats(): Resource<List<Chat>>
}