package com.luke.petpal.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.luke.petpal.domain.data.Message
import com.luke.petpal.presentation.theme.PetPalTheme

@Composable
fun ChatScreen(
    navController: NavController,
    channelId: String,
    chatViewModel: ChatViewModel
) {
    LaunchedEffect(key1 = true) {
        chatViewModel.listenForMessages(channelId)
    }
    val messages = chatViewModel.messages.collectAsState()
    ChatMessages(
        messages = messages.value,
        onSendMessage = { message ->
            chatViewModel.sendMessage(channelId, message)
        })
}

@Composable
fun ChatMessages(
    messages: List<Message>,
    onSendMessage: (String) -> Unit,
) {
    val hideKeyboardController = LocalSoftwareKeyboardController.current
    val msg = remember {
        mutableStateOf("")
    }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            items(messages) { message ->
                ChatBubble(message = message)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(8.dp)
                .background(Color.LightGray),
            verticalAlignment = Alignment.CenterVertically
        ) {

            TextField(
                value = msg.value,
                onValueChange = { msg.value = it },
                modifier = Modifier
                    .weight(1f),
                placeholder = { Text(text = "Type a message") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        hideKeyboardController?.hide()
                    }
                )
            )
            IconButton(onClick = {
                onSendMessage(msg.value)
                msg.value = ""
            }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "send")
            }

        }
    }
}

@Composable
fun ChatBubble(message: Message) {
    val isCurrentUser = message.senderId == Firebase.auth.currentUser?.uid
    val bubbleColor = if (isCurrentUser) {
        Color.Blue
    } else {
        Color.Green
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        val alignment = if (isCurrentUser) Alignment.CenterStart else Alignment.CenterEnd

        Box(contentAlignment = alignment) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .background(color = bubbleColor, RoundedCornerShape(8.dp))
            ) {
                Text(
                    text = message.message,
                    color = Color.White,
                    modifier = Modifier.padding(8.dp)
                )
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    PetPalTheme {
//        ChatScreen(navController = rememberNavController(), channelId = 0.toString(), chatViewModel = null)
        val mockMessages = listOf(
            Message(
                message = "Hello! How are you?",
                senderId = "otherUserId",
                createdAt = System.currentTimeMillis()
            ),
            Message(
                message = "I'm good, thanks! How about you?",
                senderId = "currentUserId",
                createdAt = System.currentTimeMillis()
            ),
            Message(
                message = "Doing well! What are you up to?",
                senderId = "otherUserId",
                createdAt = System.currentTimeMillis()
            )
        )

            ChatScreen(messages = mockMessages, onSendMessage = {})
    }
}

@Composable
fun ChatScreen(messages: List<Message>, onSendMessage: (String) -> Unit) {
    var newMessage by remember { mutableStateOf("") }
    val scrollState = rememberLazyListState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Messages list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            state = scrollState,
            reverseLayout = true
        ) {
            items(messages) { message ->
                ChatMessageItem(message = message)
            }
        }

        // Input field and send button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = newMessage,
                onValueChange = { newMessage = it },
                placeholder = { Text("Type a message...") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )

            Button(
                onClick = {
                    if (newMessage.isNotBlank()) {
                        onSendMessage(newMessage)
                        newMessage = ""
                    }
                }
            ) {
                Text("Send")
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: Message) {
    val isCurrentUser = message.senderId == "currentUserId" // Replace with actual user ID

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Message bubble
        Box(
            modifier = Modifier
                .padding(4.dp)
                .background(
                    color = if (isCurrentUser) Color(0xFFDCF8C6) else Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(8.dp)
        ) {
            Text(text = message.message)
        }
    }
}