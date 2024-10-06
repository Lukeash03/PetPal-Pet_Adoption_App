package com.luke.petpal.domain.data

data class Message(
    val senderId: String = "",
    val petOwnerId: String = "",
    val message: String = "",
    val createdAt: Long = System.currentTimeMillis(),
)