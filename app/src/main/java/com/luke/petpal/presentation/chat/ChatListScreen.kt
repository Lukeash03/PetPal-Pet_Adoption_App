package com.luke.petpal.presentation.chat

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luke.petpal.data.models.Resource
import com.luke.petpal.presentation.components.ChatListCard
import com.luke.petpal.presentation.components.ChatListItem
import com.luke.petpal.presentation.components.ShimmerListItem
import kotlinx.coroutines.delay

@Composable
fun ChatListScreen(
    chatViewModel: ChatViewModel?,
    paddingValues: PaddingValues,
    onChatClick: (String, String?) -> Unit
) {

    Log.i("ChatHomeScreen", "Inside ChatHomeScreen")
    LaunchedEffect(Unit) {
        chatViewModel?.fetchChats()
    }

    val chatListState = chatViewModel?.chatListState?.collectAsState()
    Log.i("ChatHomeScreen", "chatListState = ${chatListState?.value}")

    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(key1 = true) {
        delay(2000)
        isLoading = false
    }

    Scaffold {
        Box(
            modifier = Modifier
                .padding(
                    top = it.calculateTopPadding(),
                    start = 12.dp,
                    end = 12.dp
                )
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(top = paddingValues.calculateTopPadding() - it.calculateTopPadding())
            ) {

                Text(
                    text = "Chats",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                )

                Spacer(modifier = Modifier.height(16.dp))

                when (val state = chatListState?.value) {
                    is Resource.Loading -> {
//                        CircularProgressIndicator(modifier = Modifier)
                        LazyColumn {
                            items(10) {  // Display 10 shimmer items
                                ShimmerListItem(
                                    isLoading = true,  // Pass loading state
                                    contentAfterLoading = { },
                                    modifier = Modifier.padding()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }

                    is Resource.Failure -> {
                        Text(text = "Failed to load chats")
                    }

                    is Resource.Success -> {
                        Log.i("ChatHomeScreen", "Resource.Success")
                        val chatList = state.result.sortedByDescending { chat -> chat.lastMessageTimestamp }
                        if (chatList.isEmpty()) {
                            Text(
                                text = "No chats available",
                                modifier = Modifier
                            )
                        } else {

                            LazyColumn {
                                items(chatList) { chat ->
                                    ChatListCard(
                                        chat = chat,
                                        onChatClick = {
                                            onChatClick(chat.chatId, chat.senderName)
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }

                    null -> {

                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatListScreen() {
    val chatItems = listOf(
        ChatItem("https://example.com/image1.jpg", "John Doe", "Hey, how's it going?"),
        ChatItem("https://example.com/image2.jpg", "Jane Smith", "I’ll send you the details."),
        ChatItem("https://example.com/image3.jpg", "Bob Lee", "Looking forward to it!")
    )

    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 8.dp)
    ) {
        items(chatItems) { chatItem ->
            ChatListItem(
                profileImageUrl = chatItem.profileImageUrl,
                userName = chatItem.userName,
                lastMessage = chatItem.lastMessage,
                onChatClick = { },
            )
        }
    }
}

data class ChatItem(
    val profileImageUrl: String,
    val userName: String,
    val lastMessage: String
)
