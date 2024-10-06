package com.luke.petpal.domain.data

data class Chat(
    val chatId: String = "",
    val participants: List<String> = listOf(),
    val lastMessage: String = "",
    val lastMessageTimestamp: Long = System.currentTimeMillis(),
    val senderName: String? = null,
    val senderProfileImageUrl: String? = null
) {
//    val formattedDate = Utils.getChatMessageFormattedDate(sendDate)
}
