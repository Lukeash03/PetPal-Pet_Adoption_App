package com.luke.petpal.presentation.chat

import com.luke.petpal.domain.data.ChatMessage
import com.luke.petpal.domain.data.User

data class ChatUIState(
    val chatMessageList: List<ChatMessage> = emptyList(),
    val user: User = User(),
    val message: String = ""
)
