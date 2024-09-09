package com.luke.petpal.domain.data

import java.util.Date

data class ChatMessage(
    val message: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val sendDate: Date? = null
) {
//    val formattedDate = Utils.getChatMessageFormattedDate(sendDate)
}
